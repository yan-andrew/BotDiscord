package systemtickets.service;

import messenger.build.Director;
import messenger.build.MessageBuilder;
import access.secrets.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CloseTickets {

    public static void closeTicket(String pKey, MessageChannel pChannel, User pUser, Guild pGuild) throws ExecutionException, InterruptedException, IOException {
        MessageBuilder messageBuilder = new MessageBuilder();
        Director director = new Director(messageBuilder);

        director.makeCustom("Warning", "The ticket is in the process of being closed and will be deleted soon.",
                pChannel, null);

        transcribeTicket(pKey, pChannel, pUser, pGuild);
    }

    private static String readAll(InputStream pIn) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(pIn, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }

    private static String extractJsonField(String pJson, String pField) {
        String needle = "\"" + pField + "\"";
        int i = pJson.indexOf(needle);
        if (i < 0) return null;
        int colon = pJson.indexOf(':', i);
        if (colon < 0) return null;
        int firstQuote = pJson.indexOf('"', colon + 1);
        if (firstQuote < 0) return null;
        int secondQuote = pJson.indexOf('"', firstQuote + 1);
        if (secondQuote < 0) return null;
        return pJson.substring(firstQuote + 1, secondQuote);
    }

    private static void safeDelete(Path pPath) {
        try {
            Files.deleteIfExists(pPath);
        } catch (IOException ignored) {
            // Ignore cleanup errors
        }
    }

    private static void transcribeTicket(String pKey, MessageChannel pChannel, User pUser, Guild pGuild)
            throws ExecutionException, InterruptedException, IOException {

        Process process = new ProcessBuilder(
                "python",
                "src/main/java/systemtickets/service/export_transcript.py",
                pChannel.getId(),
                Config.getToken()
        ).redirectErrorStream(false).start();

        String stdout = readAll(process.getInputStream()).trim();
        String stderr = readAll(process.getErrorStream());
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            pChannel.sendMessage("Transcript export failed.").queue();
            System.out.println("export_transcript.py failed. ExitCode=" + exitCode);
            if (!stderr.isBlank()) System.out.println("stderr:\n" + stderr);
            return;
        }

        if (stdout.isBlank()) {
            pChannel.sendMessage("Transcript export returned empty output.").queue();
            if (!stderr.isBlank()) System.out.println("stderr:\n" + stderr);
            return;
        }

        Path pythonHtmlPath = Paths.get(stdout);

        if (!Files.exists(pythonHtmlPath)) {
            pChannel.sendMessage("Transcript file was not generated.").queue();
            System.out.println("Transcript path returned but file does not exist: " + stdout);
            if (!stderr.isBlank()) System.out.println("stderr:\n" + stderr);
            return;
        }

        String html = Files.readString(pythonHtmlPath, StandardCharsets.UTF_8);

        String key = pKey.replace("ticket:close:", "").trim();
        if (key.isBlank()) key = pChannel.getId();

        File transcriptFile = File.createTempFile("ticket-" + key + "-", ".html");
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(transcriptFile), StandardCharsets.UTF_8)) {
            writer.write(html);
        }

        Path siteBase = Paths.get("site");
        Files.createDirectories(siteBase);

        Path ticketSiteDir = siteBase.resolve("ticket-" + key);
        Files.createDirectories(ticketSiteDir);

        Path indexPath = ticketSiteDir.resolve("index.html");
        Files.writeString(indexPath, html, StandardCharsets.UTF_8);

        Path xPath = ticketSiteDir.resolve("x.html");
        Files.writeString(
                xPath,
                "<!doctype html><html><head><meta charset=\"utf-8\"></head><body></body></html>",
                StandardCharsets.UTF_8
        );

        String deployUrl = null;
        Path zipPath = null;

        try {
            zipPath = Paths.get("site_deploy_" + key + ".zip");
            zipDirectory(ticketSiteDir, zipPath);

            deployUrl = deployTicket(zipPath, Config.getSite(), Config.getNetfily());
        } finally {
            if (zipPath != null) safeDelete(zipPath);
            safeDelete(indexPath);
            safeDelete(xPath);

            try {
                Files.deleteIfExists(ticketSiteDir);
            } catch (IOException ignored) {
                // Ignore cleanup errors
            }

            try {
                Files.deleteIfExists(pythonHtmlPath);
            } catch (IOException ignored) {
                // Ignore cleanup errors
            }
        }

        MessageBuilder messageBuilder = new MessageBuilder();
        Director director = new Director(messageBuilder);

        String description =
                "> Closed by: " + pUser.getAsMention() + " (" + pUser.getId() + ")\n" +
                        (deployUrl != null ? ("> Transcript URL: " + deployUrl) : "> Transcript URL: (deploy failed)");

        director.makeTicketLog(
                "Ticket (" + key + ")",
                description,
                pGuild,
                transcriptFile,
                pUser
        );

        try {
            transcriptFile.delete();
        } catch (Exception ignored) {
            // Ignore cleanup errors
        }

        if (deployUrl == null) {
            pChannel.sendMessage("Deploy failed, but transcript was saved to audit logs.").queue();
        }

        pChannel.delete().queue();
    }


    private static void zipDirectory(Path pFolder, Path pZipPath) throws IOException {
        if (Files.exists(pZipPath)) Files.delete(pZipPath);

        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(pZipPath)))) {
            Files.walk(pFolder)
                    .filter(p -> !Files.isDirectory(p))
                    .forEach(path -> {
                        String entryName = pFolder.relativize(path).toString().replace("\\", "/");
                        try (InputStream in = Files.newInputStream(path)) {
                            zos.putNextEntry(new ZipEntry(entryName));
                            in.transferTo(zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private static String deployTicket(Path pZipPath, String pSiteId, String pToken)
            throws IOException, InterruptedException {

        byte[] zipBytes = Files.readAllBytes(pZipPath);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        String url = "https://api.netlify.com/api/v1/sites/" + pSiteId + "/deploys";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/zip")
                .header("Authorization", "Bearer " + pToken)
                .POST(HttpRequest.BodyPublishers.ofByteArray(zipBytes))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            System.out.println("Netlify deploy failed: " + response.statusCode());
            System.out.println(response.body());
            return null;
        }

        return extractJsonField(response.body(), "deploy_url");
    }
}

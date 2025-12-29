import sys
import os
import asyncio
import discord
import chat_exporter
import tempfile

async def export_to_file(channel_id: int, bot_token: str):
    intents = discord.Intents.default()
    intents.message_content = True
    client = discord.Client(intents=intents)

    async def shutdown():
        try:
            await client.close()
        finally:
            await asyncio.sleep(0.3)

    @client.event
    async def on_ready():
        try:
            channel = await client.fetch_channel(channel_id)
            html = await chat_exporter.export(channel)

            if not html:
                print("Export returned empty transcript", file=sys.stderr)
                await shutdown()
                sys.exit(3)

            fd, path = tempfile.mkstemp(prefix=f"ticket-{channel_id}-", suffix=".html")
            os.close(fd)

            with open(path, "w", encoding="utf-8") as f:
                f.write(html)

            # Return file path to Java
            print(path)

        except Exception as e:
            print(f"Export error: {e}", file=sys.stderr)
            await shutdown()
            sys.exit(4)

        await shutdown()

    await client.start(bot_token)

if __name__ == "__main__":
    asyncio.run(export_to_file(int(sys.argv[1]), sys.argv[2]))

package creational;

import data.ConexionDB;

public class ConexionDBSingleton {
    private static ConexionDB instance = new ConexionDB();

    private ConexionDBSingleton() {}

    public static ConexionDB getInstance() {
        return instance;
    }
}

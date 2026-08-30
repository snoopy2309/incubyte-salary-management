package com.acme.salary.support;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Test support: is the local docker-compose Postgres accepting connections?
 * Used by {@code @EnabledIf} so database-backed tests skip cleanly when the
 * compose database is not running, keeping the suite green.
 */
public final class LocalPostgres {

    private static final String HOST = "localhost";
    private static final int PORT = 5433;

    private LocalPostgres() {
    }

    public static boolean isReachable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(HOST, PORT), 1000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

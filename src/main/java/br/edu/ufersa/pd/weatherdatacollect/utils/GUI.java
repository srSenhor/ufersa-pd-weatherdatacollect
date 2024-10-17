package br.edu.ufersa.pd.weatherdatacollect.utils;

public class GUI {

    public static void droneMenu() {
        System.out.print("""
            ======= Drone Menu =======

            [0] Collect data
            [1] Turn-off

            ===========================

            Option: 
            """);
    }

    public static void clearScreen() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                // Se estiver no Windows, usa o comando "cls" para limpar o console.
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Se estiver em outro sistema operacional (como Linux ou macOS), usa o comando "clear".
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}

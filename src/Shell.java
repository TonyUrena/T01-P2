import java.util.Scanner;

public class Shell {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String inputCommand = scanner.nextLine().trim();

            if (inputCommand.equalsIgnoreCase("exit")) {
                break;
            }

            // Para que funcione en windows debemos llamar primero a cmd.exe e indicarle
            // que vamos a pasarle un comando
            Command command = new Command("cmd.exe /c " + inputCommand);
            String result = command.ejecutar();

            if (!command.isRedirectOutput()) {
                System.out.println(result);
            }
        }

        System.out.println("Shell cerrada.");
        scanner.close();
    }
}

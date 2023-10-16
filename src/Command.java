import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Command {
    private String[] arguments;
    private String outputFile;

    public boolean isRedirectOutput() {
        return redirectOutput;
    }

    private boolean redirectOutput;

    private int pid;
    private String outputText;
    private int exitValue;

    public Command(String[] arguments, String outputFile) {
        this.arguments = arguments;
        this.outputFile = outputFile;
        this.redirectOutput = !outputFile.isEmpty();
    }

    public Command(String command) {
        String[] parts = command.split(">");
        String commandPart = parts[0].trim();
        this.arguments = commandPart.split("\\s+");
        this.outputFile = (parts.length > 1) ? parts[1].trim() : "";
        this.redirectOutput = !outputFile.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Command: ");
        sb.append(String.join(" ", arguments));
        sb.append("\nNumber of parameters: ").append(arguments.length);
        sb.append("\nParameters: ").append(String.join(", ", arguments));

        if (pid != 0) {
            sb.append("\nPID: ").append(pid);
            sb.append("\nOutput: ").append(redirectOutput ? outputFile : outputText);
            if (exitValue >= 0) {
                sb.append("\nExit Value: ").append(exitValue);
                sb.append("\nCommand completed");
            } else {
                sb.append("\nCommand still running");
            }
        }

        return sb.toString();
    }

    public String ejecutar() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(arguments);
            Process process = processBuilder.start();

            pid = getProcessId(process);
            if (redirectOutput) {
                processBuilder.redirectOutput(ProcessBuilder.Redirect.to(new java.io.File(outputFile)));
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                outputText = output.toString();
            }

            exitValue = process.waitFor();
            return redirectOutput ? "" : outputText;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error executing command";
        }
    }
    private int getProcessId(Process process) {
        try {
            // Solo funciona en sistemas UNIX
            if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
                java.lang.reflect.Field field = process.getClass().getDeclaredField("pid");
                field.setAccessible(true);
                return field.getInt(process);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}

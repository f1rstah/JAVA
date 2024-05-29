import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

public class IntegrationServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(12345);
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                Thread clientHandler = new Thread(() -> {
                    try {
                        ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                        ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

                        double start = inputStream.readDouble();
                        double end = inputStream.readDouble();
                        double h1 = inputStream.readDouble();
                        double h2 = inputStream.readDouble();

                        if (start > end) {
                            throw new Exception("Invalid data input. Start more than end");
                        }

                        if (h1 > 1000000 ||  h2 > 1000000) {
                            throw new Exception("Invalid data input. Values should be between 0.000001 and 1000000.");
                        }

                        Function<Double, Double> sinFunction = a -> Math.sin(a);

                        for (double currentH = h1; currentH <= h2; currentH += 0.1) {
                            double integral = integralTrapec(start, end, currentH, sinFunction);

                            outputStream.writeDouble(start);
                            outputStream.writeDouble(end);
                            outputStream.writeDouble(currentH);
                            outputStream.writeDouble(integral);
                            outputStream.flush();
                        }

                        outputStream.writeDouble(-1); // Метка для конца вычислений
                        outputStream.flush();

                        clientSocket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                clientHandler.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static double integralTrapec(double a, double b, double h, Function<Double, Double> function) {
        double area = 0;
        for (int i = 0; a + i * h < b; i++) {
            area += h * (0.5 * function.apply(a + i * h) + function.apply(a + (i + 1) * h));
        }
        return area;
    }
}


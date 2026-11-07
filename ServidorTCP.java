import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ServidorTCP {
    private static final int PORTA = 12345;
    private static final List<PrintWriter> clientes = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PORTA)) {
            System.out.println("Servidor TCP iniciado na porta " + PORTA + ".");
            while (true) {
                Socket socket = servidor.accept();
                new Thread(new ClienteHandler(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    private static void broadcast(String mensagem) {
        synchronized (clientes) {
            for (PrintWriter cliente : clientes) {
                cliente.println(mensagem);
            }
        }
    }

    private static class ClienteHandler implements Runnable {
        private final Socket socket;

        ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            PrintWriter out = null;
            String apelido = null;
            try (Socket conexao = socket;
                 BufferedReader in = new BufferedReader(new InputStreamReader(
                         conexao.getInputStream(), StandardCharsets.UTF_8))) {
                out = new PrintWriter(new OutputStreamWriter(
                        conexao.getOutputStream(), StandardCharsets.UTF_8), true);

                // A primeira linha enviada pelo cliente identifica o participante.
                apelido = in.readLine();
                if (apelido == null || apelido.isBlank()) {
                    return;
                }
                apelido = apelido.trim();
                synchronized (clientes) {
                    clientes.add(out);
                }
                System.out.println(apelido + " conectou-se: " + conexao.getRemoteSocketAddress());
                broadcast("[Sistema] " + apelido + " entrou no chat.");

                String mensagem;
                while ((mensagem = in.readLine()) != null) {
                    if (mensagem.equals("/sair")) {
                        break;
                    }
                    String linha = apelido + ": " + mensagem;
                    System.out.println(linha);
                    broadcast(linha);
                }
            } catch (IOException e) {
                System.out.println("Conexão encerrada: " + socket.getRemoteSocketAddress());
            } finally {
                if (out != null) {
                    synchronized (clientes) {
                        clientes.remove(out);
                    }
                }
                if (apelido != null && !apelido.isBlank()) {
                    broadcast("[Sistema] " + apelido + " saiu do chat.");
                }
            }
        }
    }
}

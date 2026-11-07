import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClienteTCP {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        try (BufferedReader teclado = new BufferedReader(new InputStreamReader(
                     System.in, StandardCharsets.UTF_8));
             Socket socket = new Socket(host, 12345);
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.print("Digite seu apelido: ");
            String apelido = teclado.readLine();
            if (apelido == null || apelido.isBlank()) {
                System.out.println("Apelido obrigatório. Conexão encerrada.");
                return;
            }
            out.println(apelido.trim());
            System.out.println("Conectado. Digite mensagens ou /sair para encerrar.");

            Thread receptor = new Thread(() -> {
                try {
                    String resposta;
                    while ((resposta = in.readLine()) != null) {
                        System.out.println(resposta);
                    }
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        System.out.println("Conexão com o servidor encerrada.");
                    }
                }
            });
            receptor.setDaemon(true);
            receptor.start();

            String mensagem;
            while ((mensagem = teclado.readLine()) != null) {
                out.println(mensagem);
                if (mensagem.equals("/sair")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Não foi possível conectar: " + e.getMessage());
        }
    }
}

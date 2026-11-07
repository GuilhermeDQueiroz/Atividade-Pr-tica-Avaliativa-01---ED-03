import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class ClienteMulticastUDP {
    private static final String GRUPO = "230.0.0.1";
    private static final int PORTA = 12346;

    public static void main(String[] args) {
        try (BufferedReader teclado = new BufferedReader(new InputStreamReader(
                     System.in, StandardCharsets.UTF_8));
             MulticastSocket socket = new MulticastSocket(PORTA)) {
            InetAddress grupo = InetAddress.getByName(GRUPO);
            InetSocketAddress enderecoGrupo = new InetSocketAddress(grupo, PORTA);
            socket.joinGroup(enderecoGrupo, null);
            System.out.print("Digite seu apelido: ");
            String apelido = teclado.readLine();
            if (apelido == null || apelido.isBlank()) {
                System.out.println("Apelido obrigatório.");
                return;
            }
            apelido = apelido.trim();
            System.out.println("No grupo " + GRUPO + ":" + PORTA + ". Digite /sair para encerrar.");

            Thread receptor = new Thread(() -> {
                byte[] buffer = new byte[2048];
                while (!socket.isClosed()) {
                    DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
                    try {
                        socket.receive(pacote);
                        String texto = new String(pacote.getData(), pacote.getOffset(),
                                pacote.getLength(), StandardCharsets.UTF_8);
                        System.out.println(texto);
                    } catch (IOException e) {
                        if (!socket.isClosed()) {
                            System.err.println("Erro ao receber: " + e.getMessage());
                        }
                        break;
                    }
                }
            });
            receptor.setDaemon(true);
            receptor.start();

            String mensagem;
            while ((mensagem = teclado.readLine()) != null) {
                if (mensagem.equals("/sair")) {
                    break;
                }
                byte[] dados = (apelido + ": " + mensagem).getBytes(StandardCharsets.UTF_8);
                if (dados.length > 2048) {
                    System.out.println("Mensagem muito longa: limite de 2048 bytes em UTF-8.");
                    continue;
                }
                socket.send(new DatagramPacket(dados, dados.length, grupo, PORTA));
            }
            socket.leaveGroup(enderecoGrupo, null);
        } catch (IOException e) {
            System.err.println("Erro no multicast UDP: " + e.getMessage());
        }
    }
}

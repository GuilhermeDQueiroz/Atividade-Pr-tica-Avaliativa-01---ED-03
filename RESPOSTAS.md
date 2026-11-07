# Atividade Prática Avaliativa 01 — ED 03

## Questões teóricas

**1. Comunicação síncrona e assíncrona.** Na comunicação síncrona, o emissor espera uma resposta antes de prosseguir com a etapa que depende dela. Por exemplo, um sistema de vendas consulta o estoque e aguarda a confirmação antes de finalizar um pedido. Na comunicação assíncrona, o emissor envia a mensagem e pode continuar trabalhando; a resposta ou o processamento ocorre depois. Por exemplo, uma loja publica um evento de compra para que um serviço de e-mail envie a confirmação sem atrasar a finalização do pedido.

**2. TCP e UDP.** O TCP estabelece uma conexão antes de transmitir dados e oferece entrega confiável e ordenada, com retransmissão quando necessário. Por isso, é adequado ao chat TCP desta atividade, no qual perder ou trocar a ordem das mensagens prejudicaria a conversa. O UDP não estabelece conexão e envia datagramas sem garantir chegada, ordem ou ausência de duplicações; a aplicação precisa tratar essas situações quando forem importantes. Ele é adequado a uma transmissão de áudio ao vivo, em que receber dados recentes rapidamente costuma ser mais útil do que esperar a retransmissão de um pacote antigo. O multicast UDP da atividade também permite que um datagrama seja dirigido a um grupo de participantes.

**3. Uma thread por cliente.** A chamada `readLine()` de um cliente pode ficar bloqueada enquanto ele não envia uma mensagem. Ao criar uma thread para cada conexão, o servidor pode esperar dados daquele cliente nessa thread e, ao mesmo tempo, continuar aceitando conexões e recebendo mensagens nas outras threads. Assim, um cliente parado não impede o atendimento dos demais. Essa solução é simples para poucos clientes, embora um número muito alto de conexões demande outras estratégias de escalabilidade.

**4. Uso de `synchronized`.** Várias threads do servidor podem adicionar, remover e percorrer a lista de clientes ao mesmo tempo. Sem coordenação, uma alteração durante o percurso pode causar erro ou produzir um envio inconsistente. Os blocos `synchronized (clientes)` fazem essas operações ocorrerem com exclusão mútua sobre a mesma lista, inclusive durante o `broadcast`.

**5. Middleware.** Middleware é uma camada de software entre componentes distribuídos que oferece serviços comuns de comunicação. Ele pode cuidar de serialização, localização dos serviços, filas, autenticação, tratamento de falhas e outras tarefas de infraestrutura. Assim, cada componente usa uma interface conhecida e concentra seu código na regra de negócio, em vez de implementar repetidamente detalhes de rede.

**6. Quando usar multicast.** Em uma rede local, um professor pode transmitir o mesmo aviso em tempo real para dezenas de computadores de um laboratório. Com multicast, o emissor envia o datagrama ao endereço do grupo, e os participantes inscritos podem recebê-lo. Isso evita que a aplicação envie separadamente uma cópia para cada destinatário. A rede precisa permitir multicast, e a aplicação deve aceitar que UDP não garante a entrega.

## Análise da execução

No chat TCP, o servidor escuta na porta 12345. Cada cliente abre uma conexão, envia o apelido como primeira linha e recebe mensagens por uma thread de leitura enquanto a thread principal lê o teclado. O servidor associa cada mensagem recebida ao apelido e a retransmite a todos os clientes conectados. O comando `/sair` encerra a conexão do cliente e remove seu canal da lista de destinatários.

No chat UDP, os participantes entram no grupo multicast `230.0.0.1:12346`. Cada mensagem é enviada como datagrama para esse grupo e pode ser recebida pelos participantes inscritos, sem servidor central. Diferentemente do TCP, o UDP não assegura que todas as mensagens cheguem ou cheguem na ordem original.

## Registro da verificação prática

Os três programas foram compilados com `javac -encoding UTF-8`. Em uma execução local, dois processos `ClienteTCP` conectaram-se ao servidor; a mensagem de um apareceu no outro e no console do servidor. O envio do apelido e o encerramento por `/sair` também foram verificados. Em outra execução, dois processos `ClienteMulticastUDP` ingressaram no mesmo grupo e a mensagem enviada por um foi recebida pelo outro. A comunicação entre computadores diferentes não foi testada, pois depende das permissões e da configuração da rede usada.

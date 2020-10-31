package behaviours;

import agents.AgenteComprador;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javax.swing.JOptionPane;

public class ComportamientoComprar extends Behaviour{
    private AID mejorVendedor;
    private int mejorPrecio;
    private int repliesCount = 0;
    private MessageTemplate mt;
    private int step = 0;
    private AgenteComprador bbAgent;
    private String tituloLibro;

    public ComportamientoComprar(AgenteComprador a) {
        bbAgent = a;
        tituloLibro = a.getBookTitle();
    }

    @Override
    public void action() {
        switch(step) {
            case 0:
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for(int i = 0; i < bbAgent.getSellerAgents().length; i++) {
                    cfp.addReceiver(bbAgent.getSellerAgents()[i]);
                }

                cfp.setContent(tituloLibro);
                cfp.setConversationId("book-trade");
                cfp.setReplyWith("cfp" + System.currentTimeMillis());
                myAgent.send(cfp);

                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                step = 1;
            break;

            case 1:
                ACLMessage reply = bbAgent.receive(mt);
                if(reply != null) {
                    if(reply.getPerformative() == ACLMessage.PROPOSE) {
                        int price = Integer.parseInt(reply.getContent());
                        if(mejorVendedor == null || price < mejorPrecio) {
                            mejorPrecio = price;
                            mejorVendedor = reply.getSender();
                        }
                    }
                    repliesCount++;
                    if(repliesCount >= bbAgent.getSellerAgents().length) {
                        step = 2;
                    }
                } else {
                    block();
                }
            break;

            case 2:
                ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                order.addReceiver(mejorVendedor);
                order.setContent(tituloLibro);
                order.setConversationId("book-trade");
                order.setReplyWith("order" + System.currentTimeMillis());
                bbAgent.send(order);

                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                MessageTemplate.MatchInReplyTo(order.getReplyWith()));

                step = 3;

            break;

            case 3:      
                reply = myAgent.receive(mt);
                if (reply != null) {
                    if (reply.getPerformative() == ACLMessage.INFORM) {
                        JOptionPane.showMessageDialog(bbAgent.gui,tituloLibro+" - $"+mejorPrecio+" comprado a: "+reply.getSender().getName(),"Lbro comprado",JOptionPane.INFORMATION_MESSAGE);
                        myAgent.doDelete();
                    }
                    else {
                        JOptionPane.showMessageDialog(bbAgent.gui, "El libro ya no esta disponible","Error",JOptionPane.INFORMATION_MESSAGE);
                    }
                    step = 4;
                }
                else {
                    block();
                }
            break;
        }
    }

    @Override
    public boolean done() {
        if (step == 2 && mejorVendedor == null) {
            JOptionPane.showMessageDialog(bbAgent.gui,tituloLibro+" no esta disponible","Intento fallido",JOptionPane.INFORMATION_MESSAGE);
        }
        return ((step == 2 && mejorVendedor == null) || step == 4);    
    }
}

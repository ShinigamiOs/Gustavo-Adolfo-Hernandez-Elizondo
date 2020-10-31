package agents;

import gui.CompradorGUI;
import jade.core.Agent;
import behaviours.ComportamientoComprar;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import javax.swing.JOptionPane;

public class AgenteComprador extends Agent {
    private String titulo;
    public CompradorGUI gui;
    private AID[] aVendedores;
    private int ticker_timer = 10000;
    private AgenteComprador thisAgent = this;

    @Override
    protected void setup() {
      
    gui = new CompradorGUI(this);
    gui.showGui();
    System.out.println("Agente Comprador: " + getAID().getName() + " listo");
    }
  
    public void comprarLibro(final String libro) {

        if(libro != null && libro.length() > 0) {
            titulo = libro;
            addBehaviour(new TickerBehaviour(this, ticker_timer) {
            @Override
            protected void onTick() {
                System.out.println("Intentando comprar: " + titulo);
                gui.msg.setText("Intentando comprar: " + titulo);
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("book-selling");
                template.addServices(sd);

                try {
                    gui.msg.setText("Espere un momento...");
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    String msgVendedores="";
                    aVendedores = new AID[result.length];
                    for(int i = 0; i < result.length; i++) {
                        aVendedores[i] = result[i].getName();
                        msgVendedores = msgVendedores+aVendedores[i].getName()+"/n";
                    }
                    gui.msg.setText(msgVendedores);
                }catch(FIPAException fe) {
                    JOptionPane.showMessageDialog(gui, fe,"Exception",JOptionPane.ERROR_MESSAGE);
                }

                myAgent.addBehaviour(new ComportamientoComprar(thisAgent));
            }
            });
        } else {
            JOptionPane.showMessageDialog(gui,"No hay vendedores para el libro: "+titulo,"Comprador",JOptionPane.INFORMATION_MESSAGE);
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        gui.dispose();
        System.out.println("Agente comprador " + getAID().getName() + " Terminado");
    }

    public AID[] getSellerAgents() {
        return aVendedores;
    }

    public String getBookTitle() {
        return titulo;
    }
}

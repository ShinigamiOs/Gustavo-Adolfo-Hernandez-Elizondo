package agents;

import java.util.HashMap;
import javax.swing.JOptionPane;
import behaviours.ComportamientoOfertar;
import behaviours.ComportamientVender;
import gui.VendedorGUI;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgenteVendedor extends Agent{

    private HashMap catalogo;
    private VendedorGUI gui;
	
    @Override
    protected void setup() {
        catalogo = new HashMap();

        gui = new VendedorGUI(this);
        gui.showGui();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("book-selling");
        sd.setName("book-trading");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        }catch(FIPAException fe) {
            JOptionPane.showMessageDialog(gui, fe);
        }
        addBehaviour(new ComportamientoOfertar(this));
        addBehaviour(new ComportamientVender(this));
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        }catch(FIPAException fe) {
            JOptionPane.showMessageDialog(gui, fe,"Exception",1);
        }
        gui.dispose();
        System.out.println("Agente vendedor: " + getAID().getName() + "Terminando");
    }

    public void updateCatalogue(final String title, final int price) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                catalogo.put(title, price);
                JOptionPane.showMessageDialog(gui,"Libro "+title + " Agregado a: $" + price,"Vendedor",1);
            }
        });
    }

    public HashMap getCatalogue() {
        return catalogo;
    }
}

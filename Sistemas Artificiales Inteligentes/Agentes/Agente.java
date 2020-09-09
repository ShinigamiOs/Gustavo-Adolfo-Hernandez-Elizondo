package agentes;

import jade.core.Agent;

public class Agente extends Agent {
    
    @Override
    protected void setup(){
        Comportamiento comportamiento = new Comportamiento();
        addBehaviour(comportamiento);
    }
    
}

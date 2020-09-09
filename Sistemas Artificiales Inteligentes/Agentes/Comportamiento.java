package agentes;

import jade.core.behaviours.Behaviour;

public class Comportamiento extends Behaviour{
    
    int cont = 0;
    
    @Override
    public void action(){
        System.out.println("contador: "+cont);
        cont++;
    }
    
    @Override
    public boolean done(){
        return cont > 99;
    }
    
}

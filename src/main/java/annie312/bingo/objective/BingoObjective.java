package annie312.bingo.objective;

import org.bukkit.Material;

public class BingoObjective {

    private final Material material;
    private boolean completed;

    public BingoObjective(Material material){
        this.material = material;
        this.completed = false;
    }

    public Material getMaterial(){
        return material;
    }

    public boolean isCompleted(){
        return completed;
    }

    public void complete(){
        if(completed)
            return;

        completed = true;
    }

}
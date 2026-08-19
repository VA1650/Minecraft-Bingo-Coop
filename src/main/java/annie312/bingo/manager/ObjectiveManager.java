package annie312.bingo.manager;


import annie312.bingo.objective.BingoObjective;

import org.bukkit.Material;


import java.util.*;
import java.util.stream.Collectors;



public class ObjectiveManager {



    private final Map<Material, BingoObjective> objectives =
            new LinkedHashMap<>();







    public void add(Material material){


        if(material == Material.AIR)
            return;


        objectives.put(
                material,
                new BingoObjective(material)
        );

    }








    public void remove(Material material){

        objectives.remove(material);

    }








    public void clear(){

        objectives.clear();

    }








    public boolean contains(Material material){

        return objectives.containsKey(material);

    }

    public Collection<BingoObjective> getAll(){

        return Collections.unmodifiableCollection(
                objectives.values()
        );

    }








    public int size(){

        return objectives.size();

    }








    public int completed(){


        int result = 0;


        for(BingoObjective objective :
                objectives.values()){


            if(objective.isCompleted())
                result++;

        }


        return result;

    }








    public int remaining(){

        return size() - completed();

    }








    public boolean finished(){

        return !objectives.isEmpty()
                &&
                completed() == size();

    }








    public boolean complete(Material material){
        BingoObjective objective = objectives.get(material);

        if(objective == null || objective.isCompleted())
            return false;

        objective.complete();
        return true;
    }








    public void generateRandom(
            int amount
    ){


        clear();



        List<Material> pool =
                Arrays.stream(Material.values())

                        .filter(Material::isItem)

                        .filter(m ->
                                m != Material.AIR
                        )

                        .filter(m ->
                                !m.name()
                                        .contains("SPAWN_EGG")
                        )

                        .filter(m ->
                                !m.name()
                                        .contains("COMMAND")
                        )

                        .collect(Collectors.toList());



        Collections.shuffle(pool);



        for(int i = 0;
            i < amount && i < pool.size();
            i++){


            add(
                    pool.get(i)
            );

        }

    }

}
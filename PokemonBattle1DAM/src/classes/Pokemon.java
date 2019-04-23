package classes;

import exceptions.InvalidGenreException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cenec
 */
public class Pokemon extends LivingBeing {
    private PokemonType type;
    private String species;
    private byte level;
    private short lifePoints;
    private int experiencePoints;
    private int id;
    public enum PokemonType{
        FIRE,
        WATER,
        PLANT
    };

    /**
     *  Pokemon basic constructor with all data
     * @param id numeric id
     * @param n name
     * @param g gender
     * @param d description
     * @param type type of PokemonType enum
     * @param lifePoints 0 - 100
     * @param sp species
     * @throws InvalidGenreException 
     */
    public Pokemon(String n, char g, String d,PokemonType type, short lifePoints,String sp, int id) throws InvalidGenreException {
        super(n, g, d);
        this.species=sp;
        this.type = type;
        this.level = 1;
        this.experiencePoints=0;
        this.lifePoints = lifePoints;
        this.id=id;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(int experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public PokemonType getType() {
        return type;
    }

    public void setType(PokemonType type) {
        this.type = type;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public short getLifePoints() {
        return lifePoints;
    }

    public void setLifePoints(short lifePoints) {
        this.lifePoints = lifePoints;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public String print(){
        String genero;
        if(getGenre()=='m'){
            genero="Male";
        }else{
            genero="Female";
        }
        return "\n\tName: "+getName()
                + "\n\tGenre: "+genero
                + "\n\tDescription: "+getDescription()
                + "\n\tType: "+getType()
                + "\n\tLife Points: "+getLifePoints()
                + "\n\tSpecies: "+getSpecies()
                + "\n\tLevel: "+getLevel()
                + "\n\tId: "+getId();
    }
    
    
}

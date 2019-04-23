/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import classes.Pokemon;
import classes.Pokemon.PokemonType;
import static classes.Pokemon.PokemonType.FIRE;
import static classes.Pokemon.PokemonType.PLANT;
import static classes.Pokemon.PokemonType.WATER;
import classes.User;
import classes.User.AccessLevel;
import static classes.User.AccessLevel.BASIC;
import static classes.User.AccessLevel.PREMIUM;
import exceptions.InvalidGenreException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cenec
 */
public class PokemonGame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Variables needed to work
        Scanner sc=new Scanner(System.in);
        
        Connection connection=null;
                try {
             connection=DriverManager.getConnection(
                    "jdbc:mysql://85.214.120.213:3306/pokemonbattle1dam"
                    ,"1dam","1dam");
        } catch (SQLException ex) {
                    System.err.println("La conexion  a bd ha fallado");
        }
        
        String menu="\n\nChoose action by number:"
                +"\n\t0 - Exit game"
                +"\n\t1 - Register new User"
                +"\n\t2 - Login User";
        int action=0;
        User user=null;
        do{
            if(user!=null){
                    menu="\n\nHello, "+user.getName()+". Choose action by number:"
                            +"\n\t0 - Exit game"
                            +"\n\t1 - Show Pokemon";
                    if(user.getPokemon()==null){
                        menu+="\n\t1 - Register new pokemon";
                    }else{
                        //TODO give the option to battle, work or cure
                    }
            }
            System.out.println(menu);
            action=Integer.parseInt(sc.nextLine());
            switch(action){
                case 0:
                    System.out.println("Bye");
                    break;
                case 1:
                    //if user is still null, the user hasn't been registered nor logged in
                    if(user==null){
                        user=registerUser(sc,connection);
                    }else{
                        if(user.getPokemon()==null){
                            System.out.println("Let's register your pokemon!");
                            registerPokemon(sc,connection,user);
                        }else{
                            System.out.println(user.getPokemon().print());
                        }
                    }
                    break;
                case 2:
                    user=loginUser(sc,connection);
                 break;
                default:
                    System.out.println("Invalid option choosen");
                    break;
            }
        }while(action!=0);
    }
    
    public static void registerPokemon(Scanner sc, Connection conn, User user){
            //1 - Ask for all of pokemon data via Scanner
            boolean repeat;
            do{
                repeat=false;
            
                try{
                    //1 - Ask for all of pokemon data via Scanner
                    System.out.println("Write the name");
                    String name=sc.nextLine();
                    System.out.println("What genre it is?");
                    char genre=sc.nextLine().charAt(0);
                    byte genreBoolean=0;
                    if(genre=='f'||genre=='F'){
                        genreBoolean=0;
                    }else if (genre=='m'||genre=='M'){
                        genreBoolean=1;
                    }else{
                        throw new Exception("Error on genre");
                    }
                    System.out.println("Write a little description");
                    String desc=sc.nextLine();
                    System.out.println("What's the type of the pokemon?");
                    String typeInit=sc.nextLine();
                    PokemonType type=null;
                    switch(typeInit.toUpperCase()){
                        case "FIRE":
                            type=FIRE;
                        break;
                        case "WATER":
                            type=WATER;
                        break;
                        case "PLANT":
                            type=PLANT;
                        break;
                        default:
                            throw new Exception("Error on type");
                        
                    }
                    System.out.println("Write the species");
                    String species=sc.nextLine();
                    //2 - Create an statement object and insert into pokemon table. (you can skip id , as it is auto-incremented)
                    Statement registerStatement=conn.createStatement();
                    registerStatement.executeUpdate(
                        "insert into pokemon (name, genre, description, type, species, level, lifePoints, xp)"
                                + "values ('"+name+"',"+(int)genreBoolean+",'"+desc+"','"+typeInit+"','"+species+"',1,100,0)");
                    
                    //3 - Query the newly created pokemon id (query max id from pokemon table)
                    ResultSet idBBDD=registerStatement.executeQuery("select MAX(id) FROM pokemon");
                    idBBDD.next();
                    int id=idBBDD.getInt("max(id)");
                    //4 - Create a java Pokemon object with the read data and the queried id
                    Pokemon pokemonActual=new Pokemon(name, genre, desc,type, (short)100, species,id);
                    //5 - User setPokemon in User class to Link the pokemon to the user in java
                    user.setPokemon(pokemonActual);
                    //6 - Reuse Statement or create a new one to insert into pokemonUser table, where you link pokemon and user.
                    registerStatement.executeUpdate("insert into pokemonUser(user,pokemonId) values('"+user.getName()+"','"+pokemonActual.getId()+"') ");
                    registerStatement.close();
                }catch(Exception ex){
                    System.err.println("Invalid type or genre or lifePoint");
                    repeat=true;
                }
            }while(repeat);
            
    }
    
    public static User registerUser(Scanner sc, Connection conn){
         try {
         System.out.println("Tell me your username:");
        String username=sc.nextLine();
        System.out.println("Choose your Password:");
        String password=sc.nextLine();
        System.out.println("Choose your genre (m/f)");
        char genreChar=sc.nextLine().charAt(0);
        System.out.println("Tell me about you:");
        String description=sc.nextLine();
        
       
            
            User actual=new User(username,genreChar,
                    description,password,AccessLevel.BASIC,null);
            //we're goint to persist the user in the database
            //so we can log in as him/her later
            Statement registerStatement=conn.createStatement();
            registerStatement.executeUpdate(
                    "insert into user (name,genre,description,password,"
                            + "lastConnection,accessLevel) values('"+username+"',"
                            + "'"+genreChar+"','"+description+
                            "','"+password+"',"+
                            //Abbreviated if. This means:
                            //(condition?executesIfTrue:executesifFalse)
                            // ? and : are just separators
                            (actual.getLastConnection()!=null?
                            "'"+actual.getLastConnection()+"'":
                            "null")
                            +",'BASIC')");
            registerStatement.close();
            return actual;
         } catch (InvalidGenreException ex) {
                System.err.println(ex.getMessage());
                registerUser(sc,conn);
        } catch (SQLException ex) {
             System.err.println("SQL exception");
             ex.printStackTrace();
        }
         return null;
    }
    
    public static User loginUser(Scanner sc, Connection conn){
        try {
            System.out.println("What's your username?");
            String username=sc.nextLine();
            System.out.println("And your password?");
            String password=sc.nextLine();
            //First thing to do would be to retrieve the whole
            //user info from db.
             //We're using the wildcard (something you can replace
            //by anything) ? in the following statement. the String 
            //array is used to give values to the wildcards as they
            //appear (position 0 replaces the first ? , position 1 replaces
            //the second ? and so on...
            
            PreparedStatement loginStatement=
                    conn.prepareStatement("select * from user "
                            + "where name=? and password=? ");
            loginStatement.setString(1, username);
            loginStatement.setString(2, password);
            ResultSet foundUser=loginStatement.executeQuery();
            //7 - Create or reuse a Statement to query the pokemon linked to the user
            PreparedStatement idStatement=conn.prepareStatement("select pokemonId from pokemonUser where user= ?");
            idStatement.setString(1, username);
            ResultSet foundId=idStatement.executeQuery();
            foundId.next();
            //8 - Create or reuse a Statement to query all pokemon data for the recovered id
            // (select * from pokemon where id= ?)
            PreparedStatement PokemonStatement=conn.prepareStatement("select * from pokemon where id= ?");
            PokemonStatement.setInt(1, foundId.getInt("pokemonId"));
            ResultSet foundPokemon=PokemonStatement.executeQuery();
            
            if(foundUser.next()){ //User is found
                System.out.println("Login succesful!");            
                  //we get our recovered user and put it into a Java Object
                boolean tienePokemon=true;
                Pokemon pokemonActual=null;
                if(foundPokemon.next()){
                    //Pokemon pokemonActual=new Pokemon(name, genre, desc,type, (short)100, species,id);
                    PokemonType type;
                    switch(foundPokemon.getString("type").toUpperCase()){
                            case "FIRE":
                                type=FIRE;
                            break;
                            case "WATER":
                                type=WATER;
                            break;
                            case "PLANT":
                                type=PLANT;
                            break;
                            default:
                                throw new Exception("Error on type");

                        }
                    char genrePokemon;
                    if(foundPokemon.getInt("genre")==0){
                        genrePokemon='f';
                    }else{
                        genrePokemon='m';
                    }
                    pokemonActual=new Pokemon(foundPokemon.getString("name"),
                            genrePokemon,
                            foundPokemon.getString("description"),
                            type,
                            foundPokemon.getShort("lifePoints"),
                            foundPokemon.getString("species"),
                            foundPokemon.getInt("id")
                    );
                }else{
                    tienePokemon=false;
                }
                AccessLevel al=null;
                if(foundUser.getString("accessLevel")
                        .equals("BASIC")){
                    al=BASIC;
                }else if (foundUser.getString("accessLevel")
                        .equals("PREMIUM")){
                    al=PREMIUM;
                }else{
                    System.err.println("Invalid Access Level on Database");
                }
                User actual=new User(foundUser.getString("name"),
                foundUser.getString("genre").charAt(0),
                foundUser.getString("description"),
                foundUser.getString("password"),
                al,pokemonActual);
                foundUser.close();
                System.out.println("Hello, "+username+", login successful!");
               //TODO : Move actual upwards to main to be able to keep session track
               //Then add the 3 - Add pokemon 4 - Battle 5 - See Stats functions 6- Cure Pokemon
               //to the menu only when logged in
               //
               return actual;
            }else{ //User is not found
                    System.err.println("Invalid username and password");
                    loginUser(sc,conn);
            }
        } catch (SQLException ex) {
            System.err.println("SQL Exception");
            ex.printStackTrace();
        } catch (InvalidGenreException ex) {
            //This should never happen because
            //we make sure when we register the user
            //that genre can only have m or f
            System.err.println(ex.getMessage());
            System.err.println("Holy Shit! This should have never happened!");
            //We use an assert because this is a theoretically impossible situation
            assert true: "Holy Shit! This should have never happened!";
        } catch (Exception ex) {
            Logger.getLogger(PokemonGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        //If login is incorrect, return null user
        return null;
    }
    
}

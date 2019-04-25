import org.json.JSONArray;
import org.json.XML;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public class ProgramStarter{

        String[] params;

        public ProgramStarter(String[] args){
            params = args;
        }

        public void start(){

            class Neznaika extends Guest{
                public Neznaika(Eyes eyes, Gender gender, Portmone budget, Passport passport) {
                    super("Незнайка", eyes, gender, budget, passport);
                }
            }

            Apartment a1 = new Apartment(Luxority.Econom1, 1408);
            Apartment a2 = new Apartment(Luxority.Econom2, 3111);
            Hotel hotel = new Hotel("Raddison","Moscow", a1, a2);

            Guest g1 = new Neznaika(new Eyes(Color.White), Gender.Other, new Portmone((int)(Math.random()*200)),new Passport("", "", "", null) );

            Guest g2 = new Guest("Гриша", new Eyes(Color.White), Gender.Other, new Portmone((int)(Math.random()*200)),new Passport("", "", "", null));
            // А вот и анонимный класс спрятался
            Stuff g3 = new Stuff("Винтик", new Eyes(Color.White), Gender.Other, new Portmone((int)(Math.random()*200)),200,new Passport("", "", "", null)){};
            hotel.hireStuff(g3);
            g1.chooseAparts(hotel, Luxority.Econom2, g1, g2);
            g2.go(hotel, a2);
            g3.go(hotel, a2);
            Food pizza = new Food("Пицца", 10);
            a2.usePult().Eat(pizza, g2, g3);
            a2.usePult().Eat(hotel.getKitchen().getFood().get((int)(Math.random()*hotel.getKitchen().getFood().size())), g2, hotel.getStuff().get(0));
            a2.usePult().Eat(hotel.getKitchen().getFood().get((int)(Math.random()*hotel.getKitchen().getFood().size())), g2, hotel.getStuff().get(0));
            a1.usePult().Eat(hotel.getKitchen().getFood().get((int)(Math.random()*hotel.getKitchen().getFood().size())), g1, hotel.getStuff().get(0));
            a1.usePult().Eat(hotel.getKitchen().getFood().get((int)(Math.random()*hotel.getKitchen().getFood().size())), g1, hotel.getStuff().get(0));
            g1.damageEyes(new Soap( Color.Red, (int)(Math.random()*20)));
            a1.usePult().Iron((int)(Math.random()*25+1),g2,g3 );

        }

    }

    public static void main(String[] args) {
        //new Main().new ProgramStarter(args).start();
        /*Passport NeznaikaPassport = new Passport("Незнайка", "Незнайкович", "Незнайкович", new Photo(true, 13, 666, "neznaika.com"));
        Passport GrishaPassport = new Passport("Григорий", "Путин", "Владимирович", new Photo(false, 1999, 2019, "gov.com"));
        Passport KozlikPassport = new Passport("Винтик", "Цаль", "Богданович", new Photo(false, 7000, 3232, "arthas.com"));
        Passport MyPassport = new Passport("Андрей", "Шаля", "Юрьевич", new Photo(true, 187, 228, "https://vk.com/android16"));
        Passport TrumpPassport = new Passport("Donald", "Thrump", "Obamovich", new Photo(true, 24, 102, "https://vk.com/trumpchmod"));

        Hashtable<Integer, Passport> table = new Hashtable<>();
        table.put(MyPassport.hashCode(), MyPassport);
        table.put(KozlikPassport.hashCode(), KozlikPassport);
        table.put(GrishaPassport.hashCode(), GrishaPassport);
        table.put(NeznaikaPassport.hashCode(), NeznaikaPassport);

        FileController controller = new FileController("file.xml");
        controller.writeCollection(table);*/

        Scanner scanner = new Scanner(System.in);
        FileController controller = new FileController("file.xml");
        Command cmd = new Command(controller);
        System.out.println("Введите help для получения справки по командам");
        while (true) {
            try {
                if (controller.isFileExists()) {
                    if (cmd.parceCommand(scanner.nextLine()))
                        new Main().new ProgramStarter(args).start();
                }
                else{
                    System.out.println("Файл не найден!!!");
                }

            } catch (NoSuchElementException e) {
                System.out.println("Завершение программы!");
                System.exit(0);

            }
        }
    }
    public static class ProgramPauser{
        public static void pause(String message){
            try {
                TimeUnit.MILLISECONDS.sleep(300);
                System.out.println(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        public static void pauseErr(String message){
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                System.err.println(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}



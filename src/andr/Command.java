package andr;

import andr.Udp.MyPackage;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Класс обрабатывающий комманды для работы с коллекцией
 */
public class Command {

    private int userId;
    /**
     * Парсит комманду введенную пользователем, разбивая ее на аргументы, и выполняет ее
     * @param com комманда введенная пользователем
     * @return false
     */
    public MyPackage parceCommand(int userId, String com) {
        this.userId = userId;
        String jsonRegexp = "\\{\"patronymic\":\"(.+)\",\"name\":\"(.+)\",\"photo\":\\{\"isColored\":(true|false),\"width\":(\\d+),\"link\":\"(.+)\",\"height\":(\\d+)},\"sername\":\"(.+)\",\"ID\":(\\d+),\"birthDate\":\\{\"month\":\"(.+)\",\"year\":(\\d+),\"day\":(\\d+)}}";
        String insertRegexp = "insert \\d+ " + jsonRegexp;
        String elementCmdRegexp = "(add_if_min|remove|add_if_max|remove_lower) "+ jsonRegexp + "";
        String arglessCmdRegexp = "(show|info|help|exit|start|save)";
        String cmd = "";
        String[] args = new String[2];
        if(com.matches(insertRegexp)){
            String FirstArg = findMatches("\\d+", com).get(0);
            String SecondArg = findMatches(jsonRegexp, com).get(0);
            args = new String[]{FirstArg, SecondArg};
            cmd = "insert";
        }else if(com.matches(elementCmdRegexp)){
            cmd = findMatches("(add_if_min|remove|add_if_max|remove_lower)", com).get(0);
            String FirstArg = findMatches(jsonRegexp, com).get(0);
            args = new String[]{FirstArg};
        }else if(com.matches(arglessCmdRegexp)){
            cmd = com;
        }

        switch (cmd){
            case "insert": return insert(args);
            case "remove": return remove(args);
            case "add_if_min": return add_if_min(args);
            case "add_if_max": return add_if_max(args);
            case "remove_lower": return remove(args);
            case "show": return show();
            case "info": return info();
            case "help": return help();
            case "exit": return exit();
            case "start": return start();
            case "save": return save();
            default:
               return new MyPackage(404, userId, "Ошибка, введена неверная комманда".getBytes());
        }
    }

    /**
     * Добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
     * @param arguments аргументы комманды
     * @return false
     */
    public MyPackage add_if_min(String[] arguments){
        ArrayList<Passport> userPassports = (ArrayList<Passport>) Main.hashtable.entrySet().stream().filter(p->p.getValue().equals(userId)).map(p->p.getKey()).collect(Collectors.toList());
        Passport minPassport = Collections.min(userPassports);
        System.out.println(arguments[0]);
        Passport newPassport = new Passport(new JSONObject(arguments[0]));
        if(newPassport.compareTo(minPassport)<0){
            Main.hashtable.put(newPassport, userId);
        }
        MyPackage myPackage = new MyPackage(1,userId,"Комманда успешно выполнена".getBytes());
        return myPackage;
    }

    /**
     * Добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции
     * @param arguments аргументы комманды
     * @return false
     */
    public MyPackage add_if_max(String[] arguments){
        ArrayList<Passport> userPassports = (ArrayList<Passport>) Main.hashtable.entrySet().stream().filter(p->p.getValue().equals(userId)).map(p->p.getKey()).collect(Collectors.toList());
        Passport maxPassport = Collections.max(userPassports);
        Passport newPassport = new Passport(new JSONObject(arguments[0]));
        if(newPassport.compareTo(maxPassport)>0){
            Main.hashtable.put(newPassport, userId);
        }
        MyPackage myPackage = new MyPackage(2,userId,"Комманда успешно выполнена".getBytes());
        return myPackage;
    }

    /**
     * Добавить новый элемент с заданным ключом
     * @param arguments аргументы комманды
     * @return false
     */
    public MyPackage insert(String[] arguments) {//todo
        ArrayList<Passport> userPassports = (ArrayList<Passport>) Main.hashtable.entrySet().stream().filter(p->p.getValue().equals(userId)).map(p->p.getKey()).collect(Collectors.toList());
        Passport passport = new Passport(new JSONObject(arguments[1]));
        userPassports.add(Integer.parseInt(arguments[0]), passport);
        List<Map.Entry<Passport, Integer>> collect = Main.hashtable.entrySet().stream().filter(p -> !p.getValue().equals(userId)).collect(Collectors.toList());
        Main.hashtable.clear();
        collect.forEach(p->Main.hashtable.put(p.getKey(), p.getValue()));
        userPassports.forEach(p-> Main.hashtable.put(p, userId));
        return new MyPackage(3,userId,"Комманда успешно выполнена".getBytes());
    }

    /**
     * Удалить элемент из коллекции по его ключу
     * @param arguments аргументы комманды
     * @return false
     */
    public MyPackage remove(String[] arguments){
        Passport passport = new Passport(new JSONObject(arguments[0]));
        ArrayList<Passport> userPassports = (ArrayList<Passport>) Main.hashtable.entrySet().stream().filter(p->p.getValue().equals(userId) && !p.getKey().equals(passport)).map(p->p.getKey()).collect(Collectors.toList());
        List<Map.Entry<Passport, Integer>> collect = Main.hashtable.entrySet().stream().filter(p -> !p.getValue().equals(userId)).collect(Collectors.toList());
        Main.hashtable.clear();
        collect.forEach(p->Main.hashtable.put(p.getKey(), p.getValue()));
        userPassports.forEach(p-> Main.hashtable.put(p, userId));
        return new MyPackage(4,userId,"Комманда успешно выполнена".getBytes());
    }

    /**
     * вывести в стандартный поток вывода все элементы коллекции в строковом представлении
     * @return false
     */
    public MyPackage show(){
        ArrayList<Passport> userPassports = (ArrayList<Passport>) Main.hashtable.entrySet().stream().filter(p->p.getValue().equals(userId)).map(p->p.getKey()).collect(Collectors.toList());
        String result = "";
        for (int i = 0; i < userPassports.size(); i++){
            result += userPassports.get(i).toString() + "\n";
            System.out.println(userPassports.iterator().next().toString());
        }
        return new MyPackage(5,userId,result.getBytes());
    }

    /**
     * Выйти из программы
     * @return false
     */
    public MyPackage exit(){
        return new MyPackage(6, userId,"Комманда успешно выполнена".getBytes());
    }

    /**
     * Вывести в информацию о коллекции
     * @return false
     */
    public MyPackage info(){//done
        try(ByteArrayOutputStream byteObject = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteObject)) {
            ArrayList<Passport> userPassports = (ArrayList<Passport>) Main.hashtable.entrySet().stream().filter(p->p.getValue().equals(userId)).map(p->p.getKey()).collect(Collectors.toList());
            objectOutputStream.writeObject(userPassports);
            String rez = String.format(
                    "Тип коллекции: %s \nТип элементов коллекции: %s\nДата инициализации: %s\nКоличество элементов: %s\nРазмер: %s байт\n",
                    userPassports.getClass().getName(),
                    "andr.Passport", new Date().toString(), userPassports.size(), byteObject.toByteArray().length
            );
            return new MyPackage(7,userId,rez.getBytes());
        } catch (IOException e) {
            return new MyPackage(404, userId, "Ошибка при определении размера памяти коллекции.".getBytes());
        }
    }

    /**
     * Запуск симмуляции
     * @return true - если в коллекции достаточное количество элементов для запуска симмуляции (3 и больше)
     */
    public MyPackage start(){//done
        ArrayList<Passport> userPassports = (ArrayList<Passport>) Main.hashtable.entrySet().stream().filter(p->p.getValue().equals(userId)).map(p->p.getKey()).collect(Collectors.toList());
        if(userPassports.size() >=3){
            return new MyPackage(8, userId, "Комманда выполнена успешно".getBytes());
        }
        else{
            return new MyPackage(404, userId, "Произошла ошибка: недостаточно элементов в коллекции".getBytes());
        }
    }

    /**
     * Выводит справку по коммандам
     * @return false
     */
    public MyPackage help(){
        String rez = "add_if_min {element}: добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
                "remove {String key}: удалить элемент из коллекции по его ключу\n" +
                "show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "add_if_max {element}: добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции\n" +
                "info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "insert {String key} {element}: добавить новый элемент с заданным ключом\n" +
                "remove_lower {element}: удалить из коллекции все элементы, меньшие, чем заданный\n" +
                "Пример json элемента:\n\n" +
                "{\"patronymic\":\"Юрьевич\",\"name\":\"Андрей\",\"photo\":{\"isColored\":true,\"width\":228,\"link\":\"https://vk.com/android16\",\"height\":187},\"sername\":\"Шаля\",\"ID\":67,\"birthDate\":{\"month\":\"Apr\",\"year\":2019,\"day\":15}}";
        return new MyPackage(9, userId, rez.getBytes());
    }
    public MyPackage load(int userId, byte[] collection){
        try(ByteArrayInputStream bais = new ByteArrayInputStream(collection);
            ObjectInputStream ois = new ObjectInputStream(bais)){
                ArrayList<Passport> userPassports = (ArrayList<Passport>) ois.readObject();
                userPassports.forEach(p->Main.hashtable.put(p,userId));
                Main.hashtable.forEach((p,i)->{
                    System.out.println(p.toString());
                    System.out.println(i);
                });
                return new MyPackage(10, userId, "Коллекция успешно загружена!".getBytes());
        }catch (Exception e){
            return new MyPackage(404,userId, "Произошла ошибка при загрузке коллекции!".getBytes());
        }

    }
    public MyPackage save(){
        ArrayList<Passport> userPassports = (ArrayList<Passport>) Main.hashtable.entrySet().stream().filter(p->p.getValue().equals(userId)).map(p->p.getKey()).collect(Collectors.toList());
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos)){
            oos.writeObject(userPassports);
            return new MyPackage(11, userId, baos.toByteArray());
        }catch (IOException e){
            System.err.println("Serialization error");
            return new MyPackage(404, userId, "Произошла ошибка при загрузки коллекции с сервера".getBytes());
        }
    }
    /*public MyPackage reg(){

    }*/
    /**
     * Ищет подстроки в строке
     * @param patterStr регулярное выражение подстроки
     * @param text строка в которой ищем patterStr
     * @return collection
     */
    public static ArrayList<String> findMatches(String patterStr, String text){
        Pattern pattern = Pattern.compile(patterStr);
        Matcher matcher = pattern.matcher(text);
        ArrayList<String> collection = new ArrayList<>();
        while(matcher.find()){
            collection.add(text.substring(matcher.start(), matcher.end()));
        }
        return collection;
    }
}
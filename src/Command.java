import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс обрабатывающий комманды для работы с коллекцией
 */
public class Command {
    /**
     * Файловый контроллер
     */
    FileController controller;

    /**
     * Конструктор
     * @param controller Файловый контроллер
     */
    Command(FileController controller){
        this.controller = controller;
    }

    /**
     * Парсит комманду введенную пользователем, разбивая ее на аргументы, и выполняет ее
     * @param com комманда введенная пользователем
     * @return false
     */
    public boolean parceCommand(String com) {
        String jsonRegexp = "\\{\"patronymic\":\"(.+)\",\"name\":\"(.+)\",\"photo\":\\{\"isColored\":(true|false),\"width\":(\\d+),\"link\":\"(.+)\",\"height\":(\\d+)},\"sername\":\"(.+)\",\"ID\":(\\d+),\"birthDate\":\\{\"month\":\"(.+)\",\"year\":(\\d+),\"day\":(\\d+)}}";
        String insertRegexp = "insert \\{\\d+} \\{" + jsonRegexp + "\\}";
        String elementCmdRegexp = "(add_if_min|remove|add_if_max|remove_lower) \\{"+ jsonRegexp + "\\}";
        String arglessCmdRegexp = "(show|info|help|exit|start)";
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
            default:
                System.out.println("Неверная команда!");
        }

        return false;
    }

    /**
     * Добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
     * @param arguments аргументы комманды
     * @return false
     */
    public boolean add_if_min(String[] arguments){
        Passport minPassport = Collections.min(controller.getCollection().values());
        Passport passport = new Passport(new JSONObject(arguments[0]));
        if(passport.compareTo(minPassport) < 0){
            controller.getCollection().put(passport.hashCode(), passport);
        }
        controller.updateFile();
        System.out.println("Сделано!");
        return false;
    }

    /**
     * Добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции
     * @param arguments аргументы комманды
     * @return false
     */
    public boolean add_if_max(String[] arguments){
        Passport maxPassport = Collections.max(controller.getCollection().values());
        Passport passport = new Passport(new JSONObject(arguments[0]));
        if(passport.compareTo(maxPassport) > 0){
            controller.getCollection().put(passport.hashCode(), passport);
        }
        controller.updateFile();
        System.out.println("Сделано!");
        return false;
    }

    /**
     * Добавить новый элемент с заданным ключом
     * @param arguments аргументы комманды
     * @return false
     */
    public boolean insert(String[] arguments) {
        Passport passport = new Passport(new JSONObject(arguments[1]));
        ArrayList<Passport> passports = new ArrayList<>();
        controller.getCollection().forEach((k,v) -> passports.add(v));
        passports.add(Integer.parseInt(arguments[0]), passport);
        controller.getCollection().clear();
        passports.forEach(p -> controller.getCollection().put(p.hashCode(),p));
        controller.updateFile();
        System.out.println("Сделано!");
        return false;
    }

    /**
     * Удалить элемент из коллекции по его ключу
     * @param arguments аргументы комманды
     * @return false
     */
    public boolean remove(String[] arguments){
        Passport passport = new Passport(new JSONObject(arguments[0]));
        controller.getCollection().remove(passport.hashCode());
        System.out.println("Сделано!");
        controller.updateFile();
        return false;
    }

    /**
     * вывести в стандартный поток вывода все элементы коллекции в строковом представлении
     * @return false
     */
    public boolean show(){
        controller.getCollection().forEach((k,v) -> System.out.println(v.toString()));
        System.out.println("Сделано!");
        return false;
    }

    /**
     * Выйти из программы
     * @return false
     */
    public boolean exit(){
        System.out.println("Завершение работы программы!");
        System.exit(0);
        return false;
    }

    /**
     * Вывести в информацию о коллекции
     * @return false
     */
    public boolean info(){
        try(ByteArrayOutputStream byteObject = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteObject)) {
            objectOutputStream.writeObject(controller.getCollection());
            System.out.println(String.format(
                    "Тип коллекции: %s \nТип элементов коллекции: %s\nДата инициализации: %s\nКоличество элементов: %s\nРазмер: %s байт\n",
                    controller.getCollection().getClass().getName(),
                    "Passport", new Date().toString(), controller.getCollection().size(), byteObject.toByteArray().length
            ));
            System.out.println("Сделано!");
        } catch (IOException e) {
            System.err.println("Ошибка при определении размера памяти коллекции.");
        }
        return false;
    }

    /**
     * Запуск симмуляции
     * @return true - если в коллекции достаточное количество элементов для запуска симмуляции (3 и больше)
     */
    public boolean start(){
        if(controller.getCollection().size() >=3){
            System.out.println("Сделано!");
            return true;
        }
        else{
            System.out.println("Недостаточно элементов в коллекции!");
            return false;
        }
    }

    /**
     * Выводит справку по коммандам
     * @return false
     */
    public boolean help(){
        System.out.println(
                "add_if_min {element}: добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
                "remove {String key}: удалить элемент из коллекции по его ключу\n" +
                "show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "add_if_max {element}: добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции\n" +
                "info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "insert {String key} {element}: добавить новый элемент с заданным ключом\n" +
                "remove_lower {element}: удалить из коллекции все элементы, меньшие, чем заданный\n" +
                "Пример json элемента:\n\n" +
                "{\"patronymic\":\"Юрьевич\",\"name\":\"Андрей\",\"photo\":{\"isColored\":true,\"width\":228,\"link\":\"https://vk.com/android16\",\"height\":187},\"sername\":\"Шаля\",\"ID\":67,\"birthDate\":{\"month\":\"Apr\",\"year\":2019,\"day\":15}}");
        System.out.println("Сделано!");
        return false;
    }

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
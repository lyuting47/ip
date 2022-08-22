import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Duke {

    private static void printSpacer() {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    private static ArrayList<Task> tasks = new ArrayList<>();
    private static int tasksLength = 0;

    private static boolean cont = true;

    private static boolean startsWith(String cmd, String prefix) {
        String[] stuff = cmd.split(" ");
        return stuff[0].equals(prefix);
    }

    private static void printTaskList() {
        if (tasksLength == 0) {
            System.out.println("No tasks yet~");
        } else {
            System.out.println("Here are the tasks in your list:");
            for (int i = 1; i <= tasksLength; i++) {
                System.out.println(i + "." + tasks.get(i - 1).toString());
            }
        }
        printSpacer();
    }

    private static void greet() {
        printSpacer();
        System.out.println(":D Hello! This is Duke! What can I do for you today?");
        printSpacer();
    }

    private static void initializeDir() {
        try {
            Path dirPath = Paths.get("data", "tasks.txt");
            Files.createDirectory(dirPath);
        } catch (FileAlreadyExistsException e) {
            // no need to do anything if dir exists
        } catch (IOException e) {
            System.out.println(":( An error occurred while creating a directory to save your tasks!");
            printSpacer();
        }
    }

    private static void initializeFile() {
        try {
            Path filePath = Paths.get("data", "tasks.txt");
            Files.createFile(filePath);
            System.out.println("An empty task list has been created for you!");
            printSpacer();
        } catch (FileAlreadyExistsException e) {
            System.out.println("A previous save file was found. Loading your saved tasks...");
            printSpacer();
        } catch (IOException e) {
            System.out.println(":( An error occurred while creating a file to save your tasks!");
            printSpacer();
        }
    }

    private static void addTaskFromFileToWorkingList(String t) {
        String[] entries = t.split(" ");
        switch (entries[0]) {
        case "T":
            if (entries[1].equals("1")) {
                tasks.add(new Todo(entries[2], true));
            } else {
                tasks.add(new Todo(entries[2], false));
            }
            tasksLength++;
            break;

        case "D":
            if (entries[1].equals("1")) {
                tasks.add(new Deadline(entries[2], entries[3],true));
            } else {
                tasks.add(new Deadline(entries[2], entries[3],false));
            }
            tasksLength++;
            break;

        case "E":
            if (entries[1].equals("1")) {
                tasks.add(new Event(entries[2], entries[3],true));
            } else {
                tasks.add(new Event(entries[2], entries[3],false));
            }
            tasksLength++;
            break;
        }
    }

    private static void readFile() {
        try {
            Path filePath = Paths.get("data", "tasks.txt");
            List<String> previousTasks = Files.readAllLines(filePath);
            previousTasks.forEach(t -> addTaskFromFileToWorkingList(t));
            System.out.println("Your previously saved tasks have been loaded successfully!");
            printSpacer();
        } catch (IOException | IndexOutOfBoundsException e) {
            System.out.println(":( An error occurred while reading your previously saved tasks!");
            printSpacer();
        }
    }

    private static void updateFile() {
        try {
            Path filePath = Paths.get("data", "tasks.txt");
            ArrayList<String> taskDescriptions = new ArrayList<>();
            tasks.forEach(t -> taskDescriptions.add(t.toSaveData()));
            Files.write(filePath, taskDescriptions);
        } catch (IOException e) {
            System.out.println(":( An error occurred while saving your tasks!");
            printSpacer();
        }
    }

    private static void goodbye() {
        System.out.println(":) Bye. Hope to see you again soon!");
        printSpacer();
        cont = false;
    }

    private static void addTodo(String s) throws EmptyTodoException {
        if (s.length() <= 5) {
            throw new EmptyTodoException();
        }

        String result = s.substring(5);
        tasks.add(new Todo(result));
        tasksLength++;
        System.out.println("Got it. I've added this task:");
        System.out.println(tasks.get(tasksLength - 1).toString());
        System.out.println("Now you have " + tasksLength + " tasks in your list.");
        printSpacer();
    }

    private static void addDeadline(String s) throws DeadlineFormatException {
        if (s.length() <= 9) {
            throw new DeadlineFormatException();
        }
        String[] stuff = s.substring(9).split(" /by ");
        if (stuff.length < 2) {
            throw new DeadlineFormatException();
        }
        tasks.add(new Deadline(stuff[0], stuff[1]));
        tasksLength++;
        System.out.println("Got it. I've added this task:");
        System.out.println(tasks.get(tasksLength - 1).toString());
        System.out.println("Now you have " + tasksLength + " tasks in your list.");
        printSpacer();
    }

    private static void addEvent(String s) throws EventFormatException {
        if (s.length() <= 6) {
            throw new EventFormatException();
        }
        String[] stuff = s.substring(6).split(" /at ");
        if (stuff.length < 2) {
            throw new EventFormatException();
        }
        tasks.add(new Event(stuff[0], stuff[1]));
        tasksLength++;
        System.out.println("Got it. I've added this task:");
        System.out.println(tasks.get(tasksLength - 1).toString());
        System.out.println("Now you have " + tasksLength + " tasks in your list.");
        printSpacer();
    }

    private static void markTask(String s) throws TaskNumberException {
        try {
            int n = Integer.parseInt(s.substring(5));
            if (n > tasksLength) {
                throw new TaskNumberException();
            } else {
                tasks.get(n - 1).setDone(true);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println(tasks.get(n - 1).toString());
                printSpacer();
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new TaskNumberException();
        }
    }

    private static void unmarkTask(String s) throws TaskNumberException {
        try {
            int n = Integer.parseInt(s.substring(7));
            if (n > tasksLength) {
                throw new TaskNumberException();
            } else {
                tasks.get(n - 1).setDone(false);
                System.out.println("Okie, I've marked this task as not done yet:");
                System.out.println(tasks.get(n - 1).toString());
                printSpacer();
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new TaskNumberException();
        }
    }

    private static void deleteTask(String s) throws TaskNumberException {
        try {
            int n = Integer.parseInt(s.substring(7));
            if (n > tasksLength) {
                throw new TaskNumberException();
            } else {
                Task t = tasks.remove(n - 1);
                tasksLength--;
                System.out.println("Okie, I've deleted this task:");
                System.out.println(t.toString());
                System.out.println("Now you have " + tasksLength + " tasks in your list.");
                printSpacer();
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new TaskNumberException();
        }
    }

    private static void parseCommand(String s) throws InvalidCommandException {

        if (s.equals("list")) {
            printTaskList();
        } else if (s.equals("bye")) {
            goodbye();
        } else if (startsWith(s, "mark")) {

            try {
                markTask(s);
            } catch (TaskNumberException e) {
                System.out.println(":( Oops! Please enter a valid task number!");
                System.out.println("You currently have " + tasksLength + " tasks.");
                printSpacer();
            }

        } else if (startsWith(s, "unmark")) {

            try {
                unmarkTask(s);
            } catch (TaskNumberException e) {
                System.out.println(":( Oops! Please enter a valid task number!");
                System.out.println("You currently have " + tasksLength + " tasks.");
                printSpacer();
            }

        } else if (startsWith(s, "todo")) {

            try {
                addTodo(s);
            } catch (EmptyTodoException e) {
                System.out.println(":( Oops! The description of a todo cannot be empty!");
                printSpacer();
            }

        } else if (startsWith(s, "deadline")) {

            try {
                addDeadline(s);
            } catch (DeadlineFormatException e) {
                System.out.println(":( Oops! That's not the right way to set a deadline!");
                System.out.println("Please use this format: \"deadline <description> /by <time>\"");
                printSpacer();
            }

        } else if (startsWith(s, "event")) {

            try {
                addEvent(s);
            } catch (EventFormatException e) {
                System.out.println(":( Oops! That's not the right way to set an event!");
                System.out.println("Please use this format: \"event <description> /at <time>\"");
                printSpacer();
            }

        } else if (startsWith(s, "delete")) {

            try {
                deleteTask(s);
            } catch (TaskNumberException e) {
                System.out.println(":( Oops! Please enter a valid task number!");
                System.out.println("You currently have " + tasksLength + " tasks.");
                printSpacer();
            }

        } else {
            throw new InvalidCommandException(s);
        }

        updateFile();
    }

    public static void main(String[] args) {

        greet();
        initializeDir();
        initializeFile();
        readFile();
        Scanner sc = new Scanner(System.in);

        while (cont) {
            try {
                String s = sc.nextLine();
                parseCommand(s);
            } catch (InvalidCommandException e) {
                System.out.println(":( Sorry I don't understand the command: " + e);
                printSpacer();
            }
        }
    }
}



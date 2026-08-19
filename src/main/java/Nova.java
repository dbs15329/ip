import java.util.Scanner;

public class Nova {
    public static void main(String[] args) {
        String line = "____________________________________________________________";
        Scanner sc = new Scanner(System.in);

        String[] tasks = new String[100];
        int count = 0;

        System.out.println(line);
        System.out.println(" Hello! I'm Nova");
        System.out.println(" What can I do for you?");
        System.out.println(line);

        while (true) {
            String input = sc.nextLine();

            if (input.equals("bye")) {
                System.out.println(line);
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            } else if (input.equals("list")) {
                System.out.println(line);
                for (int i = 0; i < count; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
                System.out.println(line);
            } else {
                tasks[count] = input;
                count++;
                System.out.println(line);
                System.out.println(" added: " + input);
                System.out.println(line);
            }
        }

        sc.close();
    }
}
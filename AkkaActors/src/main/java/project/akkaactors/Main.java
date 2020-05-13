package project.akkaactors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException {

        String name = "C:\\Users\\pc\\Desktop\\AkkaDialog.txt";
        BufferedWriter writer = null;
        FileWriter fw = null;

        try {

            fw = new FileWriter(name);
            writer = new BufferedWriter(fw);
            ActorSystem system = ActorSystem.create("System");
            final ActorRef bob = system.actorOf(Props.create(PersonBob.class, "Bob", writer));
            final ActorRef alice = system.actorOf(Props.create(PersonAlice.class, "Alice", bob, writer));
            final ActorRef chat = system.actorOf(Props.create(Chat.class, "Chat", alice, bob, writer));
            System.out.println("----Send request---- ");
            chat.tell(true, ActorRef.noSender());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

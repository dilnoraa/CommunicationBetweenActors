package project.akkaactors;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import static project.akkaactors.ChatState.Available;
import static project.akkaactors.ChatState.AwaitingReceipent;
import static project.akkaactors.ChatState.Online;
import static project.akkaactors.Uninitialized.Uninitialized;
import java.io.BufferedWriter;

enum ChatState {
    Available,
    AwaitingReceipent,
    Online
}

interface Data {
}

enum Uninitialized implements Data {
    Uninitialized
}

public class Chat extends AbstractFSM<ChatState, Data> {

    public String name;
    public ActorRef alice;
    public ActorRef bob;
    public BufferedWriter writer;
    private String message;

    public Chat(String name, ActorRef alice, ActorRef bob, BufferedWriter writer) {
        this.name = name;
        this.alice = alice;
        this.bob = bob;
        this.writer = writer;
    }

    {

        startWith(Available, Uninitialized);

        when(Available,
                matchEvent(Boolean.class,
                        (noData, noData2) -> {
                            alice.tell("available", self());
                            bob.tell("available", self());
                            System.out.println("----Chat is available--- ");
                            return stay();
                        }
                )
        );

        when(Available,
                matchEvent(String.class,
                        (noData, noData2) -> {
                            message = "2) no recipient \n";
                            System.out.println(message);
                            writer.write(message);
                            return goTo(AwaitingReceipent).replying(message);
                        }
                )
        );

        when(AwaitingReceipent,
                matchEvent(String.class,
                        (noData, noData2) -> {
                            System.out.println("----Bob connected to Chat--- ");
                            alice.tell(true, self());
                            return goTo(Online);
                        }
                )
        );

        when(Online,
                matchEvent(String.class,
                        (noData, noData2) -> {
                            System.out.println("----Chat online--- ");
                            return stay();

                        }
                )
        );

        initialize();
    }
}

package project.akkaactors;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import static project.akkaactors.PersonState.Communicating;
import static project.akkaactors.PersonState.Connected;
import static project.akkaactors.PersonState.NeedChat;
import static project.akkaactors.PersonState.Talked;
import static project.akkaactors.UninitializedA.UninitializedA;
import java.io.BufferedWriter;

enum PersonState {
    NeedChat,
    Connected,
    Communicating,
    Talked
}

interface DataA {
}

enum UninitializedA implements DataA {
    UninitializedA
}

class PersonAlice extends AbstractFSM<PersonState, DataA> {

    public String name;
    public ActorRef bob;
    BufferedWriter writer;
    private String message;

    public PersonAlice(String name, ActorRef bob, BufferedWriter writer) {
        this.name = name;
        this.bob = bob;
        this.writer = writer;
    }

    {
        startWith(NeedChat, UninitializedA);

        when(NeedChat,
                matchEvent(String.class,
                        (noData, noData2) -> {
                            message = "1) Hi \n";
                            System.out.println(message);
                            writer.write(message);
                            return goTo(Connected).replying(message);
                        }
                )
        );

        when(Connected,
                matchEvent(String.class,
                        (noData, noData2) -> {
                            System.out.println("----Alice is still at the connected state--- ");
                            return stay();
                        }
                )
        );

        when(Connected,
                matchEvent(Boolean.class,
                        (noData, noData2) -> {
                            message = "3) Hi, Bob \n";
                            System.out.println(message);
                            writer.append(message);
                            bob.tell(message, self());
                            return goTo(Communicating).replying("connected");
                        }
                )
        );

        when(Communicating,
                matchEvent(String.class,
                        (noData, noData2) -> {
                            System.out.println("----Alice  talked---- ");

                            return goTo(Talked).replying("communicated");
                        }
                )
        );

        when(Talked,
                matchEvent(String.class,
                        (noData, noData2) -> {
                            System.out.println("----Alice is disconnected---- ");
                            return stop().replying("talked");

                        }
                )
        );

        initialize();
    }
}

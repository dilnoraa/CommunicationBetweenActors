package project.akkaactors;

import akka.actor.AbstractFSM;
import static project.akkaactors.UninitializedB.UninitializedB;
import java.io.BufferedWriter;
import java.util.concurrent.TimeUnit;

enum PersonBState {
    NeedChat,
    Connected,
    Communicating,
    Talked
}

interface DataB {
}

enum UninitializedB implements DataB {
    UninitializedB
}

class PersonBob extends AbstractFSM<PersonBState, DataB> {

    public String name;
    BufferedWriter writer;
    private String message;

    public PersonBob(String name, BufferedWriter writer) {
        this.name = name;
        this.writer = writer;
    }

    {

        startWith(PersonBState.NeedChat, UninitializedB);

        when(PersonBState.NeedChat,
                matchEvent(String.class,
                        (noData, noData2) -> {
                            TimeUnit.SECONDS.sleep(4);
                            System.out.println("----Bob is going to connect--- ");
                            return goTo(PersonBState.Connected).replying("Bob");
                        }
                )
        );

        when(PersonBState.Connected,
                matchEvent(String.class,
                        (noData, noData2) -> {
                            message = "4) Hi, Alice \n";
                            System.out.println(message);
                            writer.write(message);
                            return goTo(PersonBState.Communicating).replying(message);
                        }
                )
        );

        when(PersonBState.Communicating,
                matchEvent(String.class,
                        (noData, noData2) -> {
                            System.out.println("----Bob talked---- ");
                            return goTo(PersonBState.Talked).replying("talked");
                        }
                )
        );

        when(PersonBState.Talked,
                matchEvent(String.class,
                        (noData, noData2) -> {
                            System.out.println("----Bob is disconnected---- ");
                            if (writer != null) {
                                writer.close();
                            }
                            return stop();
                        }
                )
        );

        initialize();
    }
}

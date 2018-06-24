package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.util.Timeout;
import play.Logger;
import play.libs.akka.InjectedActorSupport;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;

public class ChallengeParentActor extends AbstractActor implements InjectedActorSupport {

    private final Timeout timeout = new Timeout(2, TimeUnit.SECONDS);

    public static class Create {
        final String id;
        final String challengeId;
        final String valueId;
        final String valueType;
        final String sectionId;
        public Create(String id, String challengeId, String valueId, String valueType, String sectionId) {
            this.id = id;
            this.challengeId = challengeId;
            this.valueId = valueId;
            this.valueType = valueType;
            this.sectionId = sectionId;
        }
    }

    private final ChallengeActor.Factory childFactory;

    @Inject
    public ChallengeParentActor(ChallengeActor.Factory childFactory) {
        this.childFactory = childFactory;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ChallengeParentActor.Create.class, create -> {
                    ActorRef child = injectedChild(() -> childFactory.create(create.id, create.challengeId, create.valueId, create.valueType, create.sectionId), "challengeActor-" + create.id);
                    CompletionStage<Object> future = ask(child, new Messages.WatchNewChallenges(), timeout);
                    pipe(future, context().dispatcher()).to(sender());
                }).build();
    }

}

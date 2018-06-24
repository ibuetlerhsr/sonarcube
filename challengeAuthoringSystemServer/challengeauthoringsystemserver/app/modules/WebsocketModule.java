package modules;

import actors.ChallengeActor;
import actors.ChallengeParentActor;
import com.google.inject.AbstractModule;
import play.libs.akka.AkkaGuiceSupport;


public class WebsocketModule extends AbstractModule implements AkkaGuiceSupport {
    @Override
    protected void configure() {
        bindActor(ChallengeParentActor.class, "challengeParentActor");
        bindActorFactory(ChallengeActor.class, ChallengeActor.Factory.class);
    }
}
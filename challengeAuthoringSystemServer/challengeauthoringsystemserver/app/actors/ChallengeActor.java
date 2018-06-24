package actors;

import akka.Done;
import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Pair;
import akka.stream.Materializer;
import akka.stream.UniqueKillSwitch;
import akka.stream.javadsl.*;
import akka.util.Timeout;
import com.google.inject.assistedinject.Assisted;
import models.DatabaseObject.challenge.*;
import scala.concurrent.duration.Duration;
import services.ChallengeService;
import services.TranslationService;
import utils.ControllerUtil;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class ChallengeActor extends AbstractActor {

    private final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private final Map<String, UniqueKillSwitch> challengeMap = new HashMap<>();

    private final String id;
    private final String challengeId;
    private final String valueId;
    private final String valueType;
    private final String sectionId;
    private final Materializer mat;

    private final ChallengeService challengeService;
    private final TranslationService translationService;

    private final Sink<String, NotUsed> hubSink;
    private final Flow<String, String, NotUsed> webSocketFlow;

    @Inject
    public ChallengeActor(Materializer mat,
                          ChallengeService challengeService,
                          TranslationService translationService,
                          @Assisted("id") String id,
                          @Assisted("challengeId") String challengeId,
                          @Assisted("valueId") String valueId,
                          @Assisted("valueType") String valueType,
                          @Assisted("sectionId") String sectionId) {

        this.id = id;
        this.challengeId = challengeId;
        this.valueId = valueId;
        this.valueType = valueType;
        this.sectionId = sectionId;
        this.mat = mat;
        this.challengeService = challengeService;
        this.translationService = translationService;


        Pair<Sink<String, NotUsed>, Source<String, NotUsed>> sinkSourcePair =
                MergeHub.of(String.class, 16)
                .toMat(BroadcastHub.of(String.class, 256), Keep.both())
                .run(mat);

        this.hubSink = sinkSourcePair.first();
        Source<String, NotUsed> hubSource = sinkSourcePair.second();


        Sink<String, CompletionStage<Done>> stringSink = Sink.foreach(this::writeToDb);

        this.webSocketFlow = Flow.fromSinkAndSourceCoupled(stringSink, hubSource)
                .watchTermination((n, stage) -> {
                    // When the flow shuts down, make sure this actor also stops.
                    stage.thenAccept(f -> context().stop(self()));
                    return NotUsed.getInstance();
                });
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.WatchNewChallenges.class, watchNewChallenges -> {
                    logger.info("Received message {}", watchNewChallenges);
                    sender().tell(webSocketFlow, self());
                })
                .match(Messages.UnwatchNewChallenges.class, unwatchNewChallenges -> {
                    logger.info("Received message {}", unwatchNewChallenges);
                })
                .match(String.class, text -> {
                    logger.info("Text: Received message {}", text);
                }).build();
    }

    private void writeToDb(String text){
        if(!this.challengeId.isEmpty()){
            handleTextType(text);
        } else {
            logger.error("No Challenge associated!");
        }
    }

    private String removeUnnecessaryPathPart(String text) {
        if(text == null) {
            return null;
        }
        return text.replace("/api/media/", "/media/");
    }

    private void handleTextType(String text){
        String content = removeUnnecessaryPathPart(text);
        switch(this.valueType){
            case "ABSTRACT":
                handleAbstract(content);
                break;
            case "SECTION":
                handleSection(content);
                break;
            case "SOLUTION":
                handleSolution(content);
                break;
            case "INSTRUCTION":
                handleInstruction(content);
                break;
            case "HINT":
                handleHint(content);
                break;
            case "RATING":
                handleRating(content);
                break;
            default:
                logger.error("Invalid type specified!");
        }
    }

    private void handleAbstract(String text){
        Abstract anAbstract = null;
        if(ControllerUtil.tryParseLong(this.valueId))
            anAbstract = this.challengeService.getAbstractById(Long.parseLong(this.valueId));
        else
            anAbstract = this.challengeService.getAbstractByChallengeId(this.challengeId);
        this.challengeService.handleAbstractCreationOnDB(challengeId, anAbstract, text, true);
    }

    private void handleRating(String text){
        Rating rating = null;
        if(ControllerUtil.tryParseLong(this.valueId))
            rating = this.challengeService.getRatingById(Long.parseLong(this.valueId));
        this.challengeService.handleRatingCreationOnDB(challengeId, rating, text);
    }

    private void handleInstruction(String text){
        Instruction instruction = null;
        Section section = null;
        ChallengeVersion challengeVersion = this.challengeService.getChallengeVersionByChallengeId(this.challengeId);
        if(!this.valueId.equals("valueId"))
             instruction = this.challengeService.getInstructionById(this.valueId);
        if(!this.sectionId.equals("sectionId"))
            section = this.challengeService.getSectionById(this.sectionId);
        if(challengeVersion.getSections() != null) {
            this.challengeService.handleInstructionCreationOnDB(challengeVersion.getSections().size(), challengeId, section, instruction, text, true);
        }

    }

    private void handleHint(String text){
        Hint hint = null;
        Section section = null;
        ChallengeVersion challengeVersion = this.challengeService.getChallengeVersionByChallengeId(this.challengeId);
        if(!this.valueId.equals("valueId"))
            hint = this.challengeService.getHintById(this.valueId);
        if(!this.sectionId.equals("sectionId"))
            section = this.challengeService.getSectionById(this.sectionId);
        this.challengeService.handleHintCreationOnDB(challengeVersion.getSections().size(), challengeId, section, hint, text, true);
    }

    private void handleSection(String text){
        Section section = null;
        ChallengeVersion challengeVersion = this.challengeService.getChallengeVersionByChallengeId(this.challengeId);
        if(!this.sectionId.equals("valueId"))
            section = this.challengeService.getSectionById(this.valueId);
        this.challengeService.handleSectionCreationOnDB(challengeId, section, text, true);
    }

    private void handleSolution(String text){
        Solution solution = null;
        if(ControllerUtil.tryParseLong(this.valueId))
            solution = this.challengeService.getSolutionById(Long.parseLong(this.valueId));
        else
            solution = this.challengeService.getSolutionByChallengeId(text);
        this.challengeService.handleSolutionCreationOnDB(challengeId, solution, text, true);
    }

    public interface Factory {
        Actor create(@Assisted("id")String id, @Assisted("challengeId")String challengeId, @Assisted("valueId")String valueId, @Assisted("valueType")String valueType, @Assisted("sectionId")String sectionId);
    }
}

package models.DatabaseObject.challenge;

import interfaces.IOrderableMarkdownItem;
import interfaces.ITranslatableAttribute;
import io.ebean.Finder;
import io.ebean.Model;
import models.DatabaseObject.BaseModel;
import models.DatabaseObject.translation.TranslatableAttribute;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Section extends Model implements ITranslatableAttribute, IOrderableMarkdownItem {
    private static final long serialVersionUID = 1L;
    private String id;
    private Integer version;
    private int sectionOrder;
    private TranslatableAttribute title;
    private TranslatableAttribute text;
    private List<MarkdownMediaReference> markdownMediaReference = new ArrayList<>();
    private ChallengeVersion challengeVersion;
    private List<Instruction> instructions = new ArrayList<>();
    private List<Hint> hints = new ArrayList<>();

    public static Finder<String, Section> find = new Finder<>(Section.class);

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    @Column(name="sectionOrder")
    public int getOrder() {
        return sectionOrder;
    }

    public void setOrder(int sectionOrder) {
        this.sectionOrder = sectionOrder;
    }

    @OneToMany(mappedBy = "section")
    public List<MarkdownMediaReference> getMarkdownMediaReferences() {
        return markdownMediaReference;
    }

    public void setMarkdownMediaReferences(List<MarkdownMediaReference> markdownMediaReference){
        this.markdownMediaReference = markdownMediaReference;
    }

    public void addMarkdownMediaReference(MarkdownMediaReference markdownMediaReference){
        this.markdownMediaReference.add(markdownMediaReference);
    }

    public MarkdownMediaReference removeMarkdownMediaReference(long markdownMediaReferenceId){
        MarkdownMediaReference markdownMediaReferenceToRemove = findMarkdownMediaReferenceById(markdownMediaReferenceId);
        markdownMediaReference.remove(markdownMediaReferenceToRemove);
        return markdownMediaReferenceToRemove;
    }

    private MarkdownMediaReference findMarkdownMediaReferenceById(long markdownMediaReferenceId){
        for (MarkdownMediaReference markdownMediaReference: markdownMediaReference) {
            if(markdownMediaReference.getId() == markdownMediaReferenceId)
                return markdownMediaReference;
        }
        return null;
    }

    @OneToOne
    @JoinColumn(name="title", nullable=true, insertable=true, updatable=true)
    public TranslatableAttribute getTitle() {
        return title;
    }

    public void setTitle(TranslatableAttribute title) {
        this.title = title;
    }

    @OneToOne
    @JoinColumn(name="text", nullable=true, insertable=true, updatable=true)
    public TranslatableAttribute getText() {
        return text;
    }

    public void setText(TranslatableAttribute text) {
        this.text = text;
    }

    @ManyToOne
    @JoinColumn(name="challengeVersionId", nullable=true, insertable=true, updatable=true)
    public ChallengeVersion getChallengeVersion() {
        return challengeVersion;
    }

    public void setChallengeVersion(ChallengeVersion challengeVersion) {
        this.challengeVersion = challengeVersion;
    }

    @OneToMany(mappedBy="section")
    public List<Hint> getHints(){
        return hints;
    }

    public void setHints(List<Hint> hints){
        this.hints = hints;
    }

    public void addHint(Hint hint){
        hints.add(hint);
    }

    public String removeHint(String hintId){
        Hint hintToRemove = findHintById(hintId);
        hints.remove(hintToRemove);
        return hintToRemove.getId();
    }

    public Hint findHintById(String hintId){
        for (Hint hint: hints) {
            if(hint.getId().equals(hintId))
                return hint;
        }
        return null;
    }

    @OneToMany(mappedBy="section")
    public List<Instruction> getInstructions(){
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions){
        this.instructions = instructions;
    }

    public void addInstruction(Instruction instruction){
        instructions.add(instruction);
    }

    public String removeInstruction(String instructionId){
        Instruction instructionToRemove = findInstructionById(instructionId);
        instructions.remove(instructionToRemove);
        return instructionToRemove.getId();
    }

    public Instruction findInstructionById(String instructionId){
        for (Instruction instruction: instructions) {
            if(instruction.getId().equals(instructionId))
                return instruction;
        }
        return null;
    }
    
    @Version
    @Column(name="version")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}

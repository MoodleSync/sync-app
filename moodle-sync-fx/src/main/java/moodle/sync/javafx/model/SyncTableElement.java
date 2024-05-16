package moodle.sync.javafx.model;

import javafx.beans.property.*;
import moodle.sync.core.model.json.Content;
import moodle.sync.core.util.MoodleAction;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class used to represent a course module/ section or a local file/ folder.
 */
public class SyncTableElement {

    private StringProperty moduleName;

    private IntegerProperty cmid;

    private IntegerProperty section;

    private IntegerProperty sectionId;

    private IntegerProperty oldPos;

    private StringProperty moduleType;

    private StringProperty existingFile;

    private StringProperty existingFileName;

    private BooleanProperty selectable;

    private BooleanProperty selected;

    private MoodleAction action;

    private IntegerProperty beforemod;

    private BooleanProperty visible;

    private BooleanProperty userVisible;

    private ObjectProperty<TimeDateElement> availabilityDateTime;

    private BooleanProperty doDownload = new SimpleBooleanProperty(false);

    private ObjectProperty<List<Path>> content;

    private IntegerProperty contextId;

    private BooleanProperty downloadable;

    private StringProperty sectionName;

    private ObjectProperty<List<Content>> contentOnline;



    public SyncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos,
                            String moduleType, Path existingFile, Boolean selectable, Boolean selected,
                            MoodleAction action, Boolean visible, Boolean userVisible){
        this.moduleName = new SimpleStringProperty(moduleName);
        this.cmid = new SimpleIntegerProperty(cmid);
        this.section = new SimpleIntegerProperty(section);
        this.sectionId = new SimpleIntegerProperty(sectionId);
        this.oldPos = new SimpleIntegerProperty(oldPos);
        this.moduleType = new SimpleStringProperty(moduleType);
        this.existingFile = new SimpleStringProperty(existingFile.toString());
        this.existingFileName = new SimpleStringProperty(existingFile.getFileName().toString());
        this.selectable = new SimpleBooleanProperty(selectable);
        this.selected = new SimpleBooleanProperty(selected);
        this.action = action;
        this.beforemod = new SimpleIntegerProperty(-1);
        this.visible = new SimpleBooleanProperty(visible);
        this.availabilityDateTime = new SimpleObjectProperty(new TimeDateElement(null, null));
        this.userVisible = new SimpleBooleanProperty(userVisible);
        this.sectionName = new SimpleStringProperty("");
        this.downloadable = new SimpleBooleanProperty(false);
        this.contentOnline = new SimpleObjectProperty<List<Content>>(new ArrayList<>());
    }

    public SyncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos,
                            String moduleType, Path existingFile, Boolean selectable, Boolean selected, MoodleAction action, Boolean visible, Integer beforemod , Boolean userVisible){
        this.moduleName = new SimpleStringProperty(moduleName);
        this.cmid = new SimpleIntegerProperty(cmid);
        this.section = new SimpleIntegerProperty(section);
        this.sectionId = new SimpleIntegerProperty(sectionId);
        this.oldPos = new SimpleIntegerProperty(oldPos);
        this.moduleType = new SimpleStringProperty(moduleType);
        this.existingFile = new SimpleStringProperty(existingFile.toString());
        this.existingFileName = new SimpleStringProperty(existingFile.getFileName().toString());
        this.selectable = new SimpleBooleanProperty(selectable);
        this.selected = new SimpleBooleanProperty(selected);
        this.action = action;
        this.beforemod = new SimpleIntegerProperty(beforemod);
        this.visible = new SimpleBooleanProperty(visible);
        this.availabilityDateTime = new SimpleObjectProperty(new TimeDateElement(null, null));
        this.userVisible = new SimpleBooleanProperty(userVisible);
        this.sectionName = new SimpleStringProperty("");
        this.downloadable = new SimpleBooleanProperty(false);
        this.contentOnline = new SimpleObjectProperty<List<Content>>(new ArrayList<>());
    }

    public SyncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos,
                            String moduleType, Path existingFile, Boolean selectable, Boolean selected,
                            MoodleAction action, Boolean visible, Integer beforemod,
                            TimeDateElement availabilityDateTime, List<Path> content,
                            Integer contextId, Boolean userVisible){
        this.moduleName = new SimpleStringProperty(moduleName);
        this.cmid = new SimpleIntegerProperty(cmid);
        this.section = new SimpleIntegerProperty(section);
        this.sectionId = new SimpleIntegerProperty(sectionId);
        this.oldPos = new SimpleIntegerProperty(oldPos);
        this.moduleType = new SimpleStringProperty(moduleType);
        this.existingFile = new SimpleStringProperty(existingFile.toString());
        this.existingFileName = new SimpleStringProperty(existingFile.getFileName().toString());
        this.selectable = new SimpleBooleanProperty(selectable);
        this.selected = new SimpleBooleanProperty(selected);
        this.action = action;
        this.beforemod = new SimpleIntegerProperty(beforemod);
        this.visible = new SimpleBooleanProperty(visible);
        this.availabilityDateTime = new SimpleObjectProperty(Objects.requireNonNullElseGet(availabilityDateTime,
                () -> new TimeDateElement(null, null)));
        this.content = new SimpleObjectProperty<List<Path>>(content);
        this.contextId = new SimpleIntegerProperty(contextId);
        this.userVisible = new SimpleBooleanProperty(userVisible);
        this.sectionName = new SimpleStringProperty("");
        this.downloadable = new SimpleBooleanProperty(false);
        this.contentOnline = new SimpleObjectProperty<List<Content>>(new ArrayList<>());
    }

    public SyncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos, String moduleType,
                            Path existingFile, Boolean selectable, Boolean selected, MoodleAction action, Boolean visible, TimeDateElement availabilityDateTime, Boolean userVisible){
        this.moduleName = new SimpleStringProperty(moduleName);
        this.cmid = new SimpleIntegerProperty(cmid);
        this.section = new SimpleIntegerProperty(section);
        this.sectionId = new SimpleIntegerProperty(sectionId);
        this.oldPos = new SimpleIntegerProperty(oldPos);
        this.moduleType = new SimpleStringProperty(moduleType);
        this.existingFile = new SimpleStringProperty(existingFile.toString());
        this.existingFileName = new SimpleStringProperty(existingFile.getFileName().toString());
        this.selectable = new SimpleBooleanProperty(selectable);
        this.selected = new SimpleBooleanProperty(selected);
        this.action = action;
        this.beforemod = new SimpleIntegerProperty(-1);
        this.visible = new SimpleBooleanProperty(visible);
        this.availabilityDateTime = new SimpleObjectProperty(availabilityDateTime);
        this.userVisible = new SimpleBooleanProperty(userVisible);
        this.sectionName = new SimpleStringProperty("");
        this.downloadable = new SimpleBooleanProperty(false);
        this.contentOnline = new SimpleObjectProperty<List<Content>>(new ArrayList<>());
    }

    public SyncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos, String moduleType,
                            Path existingFile, Boolean selectable, Boolean selected, MoodleAction action, Boolean visible, TimeDateElement availabilityDateTime,
                            Integer beforemod, Boolean userVisible){
        this.moduleName = new SimpleStringProperty(moduleName);
        this.cmid = new SimpleIntegerProperty(cmid);
        this.section = new SimpleIntegerProperty(section);
        this.sectionId = new SimpleIntegerProperty(sectionId);
        this.oldPos = new SimpleIntegerProperty(oldPos);
        this.moduleType = new SimpleStringProperty(moduleType);
        this.existingFile = new SimpleStringProperty(existingFile.toString());
        this.existingFileName = new SimpleStringProperty(existingFile.getFileName().toString());
        this.selectable = new SimpleBooleanProperty(selectable);
        this.selected = new SimpleBooleanProperty(selected);
        this.action = action;
        this.beforemod = new SimpleIntegerProperty(beforemod);
        this.visible = new SimpleBooleanProperty(visible);
        this.availabilityDateTime = new SimpleObjectProperty(availabilityDateTime);
        this.userVisible = new SimpleBooleanProperty(userVisible);
        this.sectionName = new SimpleStringProperty("");
        this.downloadable = new SimpleBooleanProperty(false);
        this.contentOnline = new SimpleObjectProperty<List<Content>>(new ArrayList<>());
    }

    public SyncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos,
                            String moduleType ,String existingFileName,
                            Boolean selectable, Boolean selected, MoodleAction action, Boolean visible, Boolean userVisible){
        this.moduleName = new SimpleStringProperty(moduleName);
        this.cmid = new SimpleIntegerProperty(cmid);
        this.section = new SimpleIntegerProperty(section);
        this.sectionId = new SimpleIntegerProperty(sectionId);
        this.oldPos = new SimpleIntegerProperty(oldPos);
        this.moduleType = new SimpleStringProperty(moduleType);
        this.existingFile = null;
        this.existingFileName = new SimpleStringProperty(existingFileName);
        this.selectable = new SimpleBooleanProperty(selectable);
        this.selected = new SimpleBooleanProperty(selected);
        this.action = action;
        this.beforemod = new SimpleIntegerProperty(-1);
        this.visible = new SimpleBooleanProperty(visible);
        this.availabilityDateTime = new SimpleObjectProperty(new TimeDateElement(null, null));
        this.userVisible = new SimpleBooleanProperty(userVisible);
        this.downloadable = new SimpleBooleanProperty(false);
        this.sectionName = new SimpleStringProperty("");
        this.contentOnline = new SimpleObjectProperty<List<Content>>(new ArrayList<>());
    }

    public SyncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos,
                            String moduleType, String lastModified, String existingFileName,
                            Boolean selectable, Boolean selected, MoodleAction action, Boolean visible, Boolean userVisible){
        this.moduleName = new SimpleStringProperty(moduleName);
        this.cmid = new SimpleIntegerProperty(cmid);
        this.section = new SimpleIntegerProperty(section);
        this.sectionId = new SimpleIntegerProperty(sectionId);
        this.oldPos = new SimpleIntegerProperty(oldPos);
        this.moduleType = new SimpleStringProperty(moduleType);
        this.existingFile = new SimpleStringProperty(lastModified);
        this.existingFileName = new SimpleStringProperty(existingFileName);
        this.selectable = new SimpleBooleanProperty(selectable);
        this.selected = new SimpleBooleanProperty(selected);
        this.action = action;
        this.beforemod = new SimpleIntegerProperty(-1);
        this.visible = new SimpleBooleanProperty(visible);
        this.availabilityDateTime = new SimpleObjectProperty(new TimeDateElement(null, null));
        this.userVisible = new SimpleBooleanProperty(userVisible);
        this.downloadable = new SimpleBooleanProperty(false);
        this.sectionName = new SimpleStringProperty("");
        this.contentOnline = new SimpleObjectProperty<List<Content>>(new ArrayList<>());
    }

    public SyncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos, String moduleType,
                            Path existingFile, Boolean selectable, Boolean selected, MoodleAction action, Integer beforemod,
                            Boolean visible, Boolean userVisible){
        this.moduleName = new SimpleStringProperty(moduleName);
        this.cmid = new SimpleIntegerProperty(cmid);
        this.section = new SimpleIntegerProperty(section);
        this.sectionId = new SimpleIntegerProperty(sectionId);
        this.oldPos = new SimpleIntegerProperty(oldPos);
        this.moduleType = new SimpleStringProperty(moduleType);
        this.existingFile = new SimpleStringProperty(existingFile.toString());
        this.existingFileName = new SimpleStringProperty(existingFile.getFileName().toString());
        this.selectable = new SimpleBooleanProperty(selectable);
        this.selected = new SimpleBooleanProperty(selected);
        this.action = action;
        this.beforemod = new SimpleIntegerProperty(beforemod);
        this.visible = new SimpleBooleanProperty(visible);
        this.availabilityDateTime = new SimpleObjectProperty(new TimeDateElement(null, null));
        this.userVisible = new SimpleBooleanProperty(userVisible);
        this.downloadable = new SimpleBooleanProperty(false);
    }

    /**
     * Method used to provide the availability of a course module as a UNIX-Timestamp.
     *
     * @return UNIX-Timestamp.
     */
    public long getUnixTimeStamp(){
        LocalDateTime time = null;
        try{
            time =  availabilityDateTime.get().getLocalTime().atDate(availabilityDateTime.get().getLocalDate());
        } catch (Exception e) {
            return 1659776301;
        }
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()/1000L;
    }

    /**
     * Providing the MoodleAction.
     *
     * @return the MoodleAction.
     */
    public MoodleAction getAction(){ return action;}

    public void setAction(MoodleAction action) {
        this.action = action;
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public StringProperty moduleNameProperty() {
        return moduleName;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public String getModuleName() {
        return this.moduleName.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setModuleName(String value) {
        this.moduleName.set(value);
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public IntegerProperty cmidProperty() {
        return cmid;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public Integer getCmid() {
        return this.cmid.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setCmid(Integer value) { this.cmid.set(value);
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public IntegerProperty sectionProperty() {
        return section;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public Integer getSection() {
        return this.section.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setSection(Integer value) { this.section.set(value);
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public IntegerProperty sectionIdProperty() {
        return sectionId;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public Integer getSectionId() {
        return this.sectionId.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setSectionId(Integer value) { this.sectionId.set(value);
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public IntegerProperty oldPosProperty() {
        return oldPos;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public Integer getOldPos() {
        return this.oldPos.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setOldPos(Integer value) { this.oldPos.set(value);
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public StringProperty existingFileProperty() {
        return existingFile;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public String getExistingFile() {
        return this.existingFile.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setExistingFile(String value) {
        this.existingFile.set(value);
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public StringProperty existingFileNameProperty() {
        return existingFileName;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public String getExistingFileName() {
        return this.existingFileName.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setExistingFileName(String value) {
        this.existingFileName.set(value);
    }
    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public StringProperty moduleTypeProperty() {
        return moduleType;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public String getModuleType() {
        return this.moduleType.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setModuleType(String value) {
        this.moduleType.set(value);
    }

    /**
     * Providing the selectedProperty.
     *
     * @return the selectedProperty.
     */
    public BooleanProperty selectableProperty() {
        return selectable;
    }

    /**
     * Proving whether the element is selected or not.
     *
     * @return True if the element is selected.
     */
    public boolean isSelectable() {
        return this.selectable.get();
    }

    /**
     * Sets the selectedProperty.
     *
     * @param value if the object is selected.
     */
    public void setSelectable(boolean value) {
        this.selectable.set(value);
    }
    /**
     * Providing the selectedProperty.
     *
     * @return the selectedProperty.
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * Proving whether the element is selected or not.
     *
     * @return True if the element is selected.
     */
    public boolean isSelected() {
        return this.selected.get();
    }

    /**
     * Sets the selectedProperty.
     *
     * @param value if the object is selected.
     */
    public void setSelected(boolean value) {
        this.selected.set(value);
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public IntegerProperty beforemodProperty() {
        return beforemod;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public Integer getBeforemod() {
        return this.beforemod.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setBeforemod(Integer value) { this.beforemod.set(value);
    }

    /**
     * Providing the visibleProperty.
     *
     * @return the visibleProprty.
     */
    public BooleanProperty visibleProperty() {
        return visible;
    }

    /**
     * Providing the visible as a boolean.
     *
     * @return the visible as a boolean.
     */
    public boolean getVisible() {
        return this.visible.get();
    }

    /**
     * Sets a new visible.
     *
     * @param value the new visible.
     */
    public void setVisible(boolean value) { this.visible.set(value);
    }

    /**
     * Providing the userVisibleProperty.
     *
     * @return the userVisibleProprty.
     */
    public BooleanProperty userVisibleProperty() {
        return userVisible;
    }

    /**
     * Providing the userVisible a boolean.
     *
     * @return the userVisible as a boolean.
     */
    public boolean getUserVisible() {
        return this.userVisible.get();
    }

    /**
     * Sets a new userVisible.
     *
     * @param value the new userVisible.
     */
    public void setUserVisible(boolean value) { this.userVisible.set(value);
    }

    /**
     * Providing the userVisibleProperty.
     *
     * @return the userVisibleProprty.
     */
    public BooleanProperty downloadableProperty() {
        return downloadable;
    }

    /**
     * Providing the userVisible a boolean.
     *
     * @return the userVisible as a boolean.
     */
    public boolean getDownloadable() {
        return this.downloadable.get();
    }

    /**
     * Sets a new userVisible.
     *
     * @param value the new userVisible.
     */
    public void setDownloadable(boolean value) { this.downloadable.set(value);
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public ObjectProperty<TimeDateElement> availabilityDateTimeProperty() {
        return availabilityDateTime;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public TimeDateElement getTimeDateElement() {
        return availabilityDateTime.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setTimeDateElement(TimeDateElement value) { this.availabilityDateTime.set(value);
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public BooleanProperty doDownloadProperty() {
        return doDownload;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public boolean getDoDownload() {
        return this.doDownload.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setDoDownload(boolean value) { this.doDownload.set(value);
    }

    public ObjectProperty<List<Path>> contentProperty() {
        return content;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public List<Path> getContent() {
        return this.content.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setContent(List<Path> value) { this.content.set(value);
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public IntegerProperty contextIdProperty() {
        return contextId;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public Integer getContextId() {
        return this.contextId.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setContextId(Integer value) { this.contextId.set(value);
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public StringProperty sectionNameProperty() {
        return sectionName;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public String getSectionName() {
        return this.sectionName.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setSectionName(String value) {
        this.sectionName.set(value);
    }

    /**
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public ObjectProperty<List<Content>> contentsOnlineProperty() {
        return contentOnline;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public List<Content> getContentsOnline() {
        return this.contentOnline.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void addContentOnline(Content value) {
        this.contentOnline.get().add(value);
    }

}

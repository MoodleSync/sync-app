package moodle.sync.javafx.model;

import javafx.beans.property.*;
import moodle.sync.core.util.MoodleAction;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class syncTableElement {

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

    private ObjectProperty<TimeDateElement> availabilityDateTime;

    private BooleanProperty delete = new SimpleBooleanProperty(false);

    public syncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos, String moduleType, Path existingFile, Boolean selectable, Boolean selected, MoodleAction action, Boolean visible){
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
    }

    public syncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos, String moduleType, Path existingFile, Boolean selectable, Boolean selected, MoodleAction action, Boolean visible, Integer beforemod){
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
    }

    public syncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos,String moduleType, Path existingFile, Boolean selectable, Boolean selected, MoodleAction action, Boolean visible, TimeDateElement availabilityDateTime){
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
    }

    public syncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos, String moduleType, Path existingFile, Boolean selectable, Boolean selected, MoodleAction action, Boolean visible, TimeDateElement availabilityDateTime, Integer beforemod){
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
    }

    public syncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos, String moduleType, Boolean selectable, Boolean selected, MoodleAction action, Boolean visible){
        this.moduleName = new SimpleStringProperty(moduleName);
        this.cmid = new SimpleIntegerProperty(cmid);
        this.section = new SimpleIntegerProperty(section);
        this.sectionId = new SimpleIntegerProperty(sectionId);
        this.oldPos = new SimpleIntegerProperty(oldPos);
        this.moduleType = new SimpleStringProperty(moduleType);
        this.existingFile = null;
        this.existingFileName = null;
        this.selectable = new SimpleBooleanProperty(selectable);
        this.selected = new SimpleBooleanProperty(selected);
        this.action = action;
        this.beforemod = new SimpleIntegerProperty(-1);
        this.visible = new SimpleBooleanProperty(visible);
        this.availabilityDateTime = new SimpleObjectProperty(new TimeDateElement(null, null));
    }

    public syncTableElement(String moduleName, Integer cmid, Integer section, Integer sectionId, Integer oldPos, String moduleType, Path existingFile, Boolean selectable, Boolean selected, MoodleAction action, Integer beforemod, Boolean visible){
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
    }

    public long getUnixTimeStamp(){
        LocalDateTime time = null;
        try{
            time =  availabilityDateTime.get().getLocalTime().atDate(availabilityDateTime.get().getLocalDate());
        } catch (Exception e) {
            return 1659776301;
        }
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()/1000L;
    }

    public MoodleAction getAction(){ return action;}

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
     * Providing the messageProperty.
     *
     * @return the messageProprty.
     */
    public BooleanProperty visibleProperty() {
        return visible;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public boolean getVisible() {
        return this.visible.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setVisible(boolean value) { this.visible.set(value);
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
    public BooleanProperty deleteProperty() {
        return delete;
    }

    /**
     * Providing the files message as a String.
     *
     * @return the files message as a String.
     */
    public boolean getDelete() {
        return this.delete.get();
    }

    /**
     * Sets a new message.
     *
     * @param value the new message.
     */
    public void setDelete(boolean value) { this.delete.set(value);
    }
}

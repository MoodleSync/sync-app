package moodle.sync.util;

import moodle.sync.javafx.model.ReturnValue;
import moodle.sync.javafx.model.TimeDateElement;
import moodle.sync.javafx.model.syncTableElement;
import moodle.sync.core.model.json.JsonConfigProvider;
import moodle.sync.core.model.json.Module;
import moodle.sync.core.model.json.ModuleAvailability;
import moodle.sync.core.model.json.Section;
import moodle.sync.core.util.MoodleAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class implementing several methods in terms of file handling and comparison.
 *
 * @author Daniel Schröter
 */
public class FileService {


    /**
     * Secures that a directory given in a given path exists. Therefore the directory could be created.
     *
     * @param p Path of the directory.
     */
    public static void directoryManager(Path p) throws Exception {
        Files.createDirectories(p);
    }


    public static List<Path> formatSectionFolder(List<Path> sectionList, Section section) {
        int remove = -1;
        for (int i = 0; i < sectionList.size(); i++) {
            String[] sectionFolder = sectionList.get(i).getFileName().toString().split("_", 2);
            if (sectionFolder[sectionFolder.length - 1].equals(section.getName())) {
                File temp = new File(sectionList.get(i).toString());
                temp.renameTo(new File((sectionList.get(i).getParent().toString() + "/" + section.getSection() + "_" + section.getName())));
                remove = i;
                break;
            }
        }
        if (remove != -1) {
            sectionList.remove(remove);
        }
        return sectionList;
    }

    /**
     * Obtaining a list containing all paths inside a directory.
     *
     * @param p Path of the directory.
     * @return list of all paths inside the directory.
     */
    public static List<Path> fileServerRequired(Path p) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
            for (Path entry : stream) {
                result.add(entry);
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encountered during the iteration, the cause is an IOException.
            throw ex.getCause();
        }
        return result;
    }


    public static List<Path> getPathsInDirectory(Path p) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
            for (Path entry : stream) {
                //When an element is a directory, a recursive-call of this method is made.
                result.add(entry);
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encounted during the iteration, the cause is an IOException
            throw ex.getCause();
        }
        return result;
    }

    //Returns List with all Moodle-Moduls of a choosen Module-type

    /**
     * Method to sort the Modules inside a Section by Module-type.
     *
     * @param type1   Module-type searched for.
     * @param section Section containing a list of Modules.
     * @return a list of Modules of the searched Module-type.
     */
    public static List<Module> getModulesByType(String type1, Section section) {
        List<Module> modules = new ArrayList<>();
        for (int i = 0; i < section.getModules().size(); i++) {
            if (section.getModules().get(i).getModname().equals(type1)) {
                modules.add(section.getModules().get(i));
            }
        }
        return modules;
    }

    public static ReturnValue findModuleInFiles(List<Path> fileList, Module module, int sectionNum, int sectionId,
                                                int position /* Substitute data.size()*/) throws Exception {
        syncTableElement element = null;
        boolean found = false;
        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).getFileName().toString().equals(module.getContents().get(0).getFilename())) {
                long onlinemodified = module.getContents().get(0).getTimemodified() * 1000;
                long filemodified = Files.getLastModifiedTime(fileList.get(i)).toMillis();
                //Check if local file is newer.
                if (filemodified > onlinemodified) {
                    found = true;
                    if (module.getAvailability() != null) {
                        var JsonB = new JsonConfigProvider().getContext(null);
                        JsonB.fromJson(module.getAvailability(), ModuleAvailability.class);
                        LocalDateTime time =
                                LocalDateTime.ofInstant(Instant.ofEpochMilli(JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""),
                                        ModuleAvailability.class).getTimeDateCondition().getT() * 1000L), ZoneId.systemDefault());
                        element = new syncTableElement(module.getName(), module.getId(), sectionNum, sectionId,
                                position, module.getModname(), fileList.get(i), true, false,
                                MoodleAction.MoodleSynchronize, getPriorityVisibility(module.getVisible() == 1,
                                JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""),
                                        ModuleAvailability.class).getConditionVisibility()),
                                new TimeDateElement(time.toLocalDate(), time.toLocalTime()), module.getId());
                    } else {
                        element = new syncTableElement(module.getName(), module.getId(), sectionNum, sectionId,
                                position, module.getModname(), fileList.get(i), true, false,
                                MoodleAction.MoodleSynchronize, module.getVisible() == 1, module.getId());
                    }
                    fileList.remove(i);
                    break;
                } else {
                    found = true;
                    if (module.getAvailability() != null) {
                        var JsonB = new JsonConfigProvider().getContext(null);
                        JsonB.fromJson(module.getAvailability(), ModuleAvailability.class);
                        LocalDateTime time =
                                LocalDateTime.ofInstant(Instant.ofEpochMilli(JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""),
                                        ModuleAvailability.class).getTimeDateCondition().getT() * 1000L), ZoneId.systemDefault());
                        element = new syncTableElement(module.getName(), module.getId(), sectionNum, sectionId,
                                position, module.getModname(), fileList.get(i), false, false,
                                MoodleAction.ExistingFile, getPriorityVisibility(module.getVisible() == 1,
                                JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""),
                                        ModuleAvailability.class).getConditionVisibility()),
                                new TimeDateElement(time.toLocalDate(), time.toLocalTime()));
                    }
                    else {
                        element = new syncTableElement(module.getName(), module.getId(), sectionNum, sectionId,
                                position, module.getModname(), fileList.get(i), false, false,
                                MoodleAction.ExistingFile, module.getVisible() == 1);
                    }
                    fileList.remove(i);
                    break;
                }
            }
        }
        if (!found) {
            element = new syncTableElement(module.getName(), module.getId(), sectionNum, sectionId, position,
                    module.getModname(), false, false, MoodleAction.NotLocalFile, module.getVisible() == 1);
        }

        return new ReturnValue(fileList, element);
    }


    private static Boolean getPriorityVisibility(Boolean visible, Boolean availability) {
        if (!visible || !availability) {
            return false;
        }
        return true;
    }

    public static boolean contains(final String[] arr, final String key) {
        return Arrays.asList(arr).contains(key);
    }
}


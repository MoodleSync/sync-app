package moodle.sync.cli;

import static java.util.Objects.nonNull;

import moodle.sync.cli.inject.ApplicationModule;
import moodle.sync.core.app.dictionary.Dictionary;
import moodle.sync.core.inject.GuiceInjector;
import moodle.sync.core.inject.Injector;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.MoodleUpload;
import moodle.sync.core.model.json.Section;
import moodle.sync.core.web.service.MoodleService;
import moodle.sync.core.web.service.MoodleUploadTemp;

import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

public class CommandLineInterface {

    private final MoodleService moodleService;

    private final Dictionary dictionary;

    private String url;

    private String token;

    private int userId;


    public static void main(String[] args) {
        Injector injector = new GuiceInjector(new ApplicationModule());

        CommandLineInterface cliTool = injector.getInstance(CommandLineInterface.class);
        cliTool.execute(args);
    }

    @Inject
    public CommandLineInterface(MoodleService moodleService, Dictionary dictionary) {
        this.moodleService = moodleService;
        this.dictionary = dictionary;
    }

    private void execute(String[] args) {
        Options options = generateDefaultOptions();

        try {
            CommandLine cmd = parseCommandLine(options, args);

            // Check for required parameters
            checkParameters(cmd);

            // Login to moodle
            String loginConfigPath = cmd.getOptionValue("l");
            login(loginConfigPath);

            // Get the dedicated course
            String courseId = cmd.getOptionValue("course");
            Course course = getCourse(courseId);

            // Get the dedicated section
            String sectionId = cmd.getOptionValue("section");
            Section section = getSection(course, sectionId);

            // Get file path to upload
            Path file = getFilePath(cmd.getOptionValue("path"));

            // Execute file upload
            upload(course, section, file);
        } catch (Exception e) {
            if (nonNull(e.getMessage())) {
                printError(e.getMessage());
            }
            printHelp();
        }
    }

    private Options generateDefaultOptions() {
        Options options = new Options();
        options.addOption("c", "course", true, dictionary.get("cli.option.course"));
        options.addOption("s", "section", true, dictionary.get("cli.option.section"));
        options.addOption("p", "path", true, dictionary.get("cli.option.path"));
        options.addOption("l", "login", true, dictionary.get("cli.option.login"));

        return options;
    }

    private CommandLine parseCommandLine(Options options, String[] args) {
        CommandLineParser parser = new DefaultParser();

        try {
            return parser.parse(options, args);
        } catch (Exception e) {
            // If parameter specified but no value given
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void checkParameters(CommandLine cmd) {
        if (!cmd.hasOption("c") || !cmd.hasOption("s") || !cmd.hasOption("p")) {
            throw new IllegalArgumentException("cli.args.incomplete");
        }
    }

    private Course getCourse(String courseId) {
        Course course;

        try {
            List<Course> courses = moodleService.getEnrolledCourses(token, userId);

            course = courses.stream()
                    .filter(c -> c.getId().toString().equals(courseId))
                    .findFirst().orElse(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("cli.moodle.external.service.error");
        }

        if (course == null) {
            throw new IllegalArgumentException("cli.moodle.course.invalid");
        }

        return course;
    }

    private Section getSection(Course course, String sectionId) {
        try {
            return moodleService.getCourseContentSection(token, course.getId(),
                    Integer.parseInt(sectionId)).get(0);
        } catch (Exception e) {
            throw new IllegalArgumentException("cli.moodle.section.invalid");
        }
    }

    private Path getFilePath(String filePath) {
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("cli.path.invalid");
        }

        return path;
    }

    private void login(String configPath) throws IllegalAccessException {
        if (nonNull(configPath) && !configPath.isEmpty() && !configPath.isBlank()) {
            // Parse config file to get credentials
            Properties moodleProps = new Properties();

            try {
                moodleProps.load(new FileInputStream(Path.of(configPath).toFile()));
            } catch (IOException e) {
                throw new IllegalArgumentException("cli.login.properties.invalid");
            }

            url = moodleProps.getProperty("url");
            token = moodleProps.getProperty("token");
        } else {
            // Read credentials from environment variables
            url = System.getenv("MOODLE_SYNC_URL");
            token = System.getenv("MOODLE_SYNC_TOKEN");
        }

        if (url == null || token == null) {
            throw new IllegalArgumentException("cli.moodle.login.invalid");
        }

        moodleService.setApiUrl(url);

        // Check if user exists
        try {
            userId = moodleService.getUserId(token);
        } catch (Exception e) {
            throw new IllegalAccessException("cli.moodle.connection.failed");
        }
    }

    private void upload(Course course, Section section, Path file) throws IOException {
        MoodleUploadTemp uploader = new MoodleUploadTemp();
        MoodleUpload upload;

        String fileName = file.getFileName().toString();

        try {
            upload = uploader.upload(fileName, file.toString(), url, token);

            moodleService.setResource(token, course.getId(), section.getSection(),
                    upload.getItemid(), null, true, fileName, -1);
        } catch (Exception e) {
            throw new IOException("cli.moodle.upload.error", e);
        }
    }

    private void printHelp() {
        String prefix = dictionary.get("cli.usage");
        String header = dictionary.get("cli.help.header");
        String footer = dictionary.get("cli.help.footer");

        HelpFormatter formatter = new HelpFormatter();
        PrintWriter writer = new PrintWriter(new PrintStream(System.out, true,
                StandardCharsets.UTF_16));

        formatter.setSyntaxPrefix(prefix);
        formatter.printHelp(writer, 80, "moodle-sync-cli", header,
                generateDefaultOptions(), 3, 5, footer, true);

        writer.flush();
    }

    private void printError(String error) {
        System.err.println(dictionary.contains(error) ? dictionary.get(error) : error);
    }
}
package moodle.sync.presenter;

import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.web.service.MoodleService;
import moodle.sync.presenter.command.ShowGuestCommand;
import moodle.sync.presenter.command.ShowSettingsCommand;
import moodle.sync.presenter.command.ShowTrainerCommand;
import moodle.sync.util.VerifyDataService;
import moodle.sync.view.LandingView;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.NotificationType;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;

import static java.util.Objects.isNull;

/**
 * Class defining the logic of the "start-page".
 *
 * @author Daniel Schröter
 */
public class LandingPresenter extends Presenter<LandingView> {

    //Used MoodleService for executing Web Service API-Calls.
    private final MoodleService moodleService;

    //Configuration providing the settings.
    private final MoodleSyncConfiguration config;

    //User's moodle token.
    private String token;

    //The moodle plattforms url.
    private String url;



    @Inject
    LandingPresenter(ApplicationContext context, LandingView view, MoodleService moodleService) {
        super(context, view);
        this.moodleService = moodleService;
        this.config = (MoodleSyncConfiguration) context.getConfiguration();
    }

    @Override
    public void initialize() {
        System.out.println("LandingPresenter initialized");

        context.getEventBus().register(this);

        //view.setOnSettings(this::onSettings);
        view.setCourse(config.recentCourseProperty());
        view.setCourses(courses());
        view.setOnUpdate(this::initCourse);

        config.recentCourseProperty().addListener((observable, oldCourse, newCourse) -> {
            selectCourse(newCourse);
        });

        /*CompletableFuture
                .runAsync(() -> {initCourse();} )
                .exceptionally(e -> {
                    logException(e, "Inititialize course " + "failed");
                    return null;
                });*/
        //CompletableFuture.supplyAsync().acc
    }

    private void initCourse() {
        try{
            if(!isNull(config.recentCourseProperty().get())) {
                selectCourse(config.recentCourseProperty().get());
            }
        } catch (Exception e){
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "Already choosen course not available");
            config.setRecentCourse(null);
        }
    }

    private void selectCourse(Course course) {
        if(moodleService.getPermissions(config.getMoodleToken(), course.getId())) {
            context.getEventBus().post(new ShowTrainerCommand(new Action() {
                @Override
                public void execute() {

                }
            }));
        } else {
            context.getEventBus().post(new ShowGuestCommand(new Action() {
                @Override
                public void execute() {

                }
            }));
        }
    }


    private List<Course> courses() {
        url = config.getMoodleUrl();
        token = config.getMoodleToken();
        //Security checks to prevent unwanted behaviour.
        if (!VerifyDataService.validateString(url) || !VerifyDataService.validateString(token)) {
            return new ArrayList<>();
        }
        List<Course> courses = List.of();
        try {
            courses = moodleService.getEnrolledCourses(token, moodleService.getUserId(token));
        }
        catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.invalidurl.message");
            config.setRecentCourse(null);
        }

        //Do not show Moodle-courses which are already over.
        if (!courses.isEmpty()) {
            courses.removeIf(item -> (item.getEnddate() != 0 && (item.getEnddate() < System.currentTimeMillis() / 1000)));
        }

        return courses;
    }

    /**
     * Method to "open" the Settings-page.
     */
    /*private void onSettings() {
        context.getEventBus().post(new ShowSettingsCommand(new Action() {
            @Override
            public void execute() {

            }
        }));
    }*/

}

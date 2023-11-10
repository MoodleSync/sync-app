/*
 * Copyright (C) 2020 TU Darmstadt, Department of Computer Science,
 * Embedded Systems and Applications Group.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package moodle.sync.javafx.view;

import static java.util.Objects.nonNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import moodle.sync.core.view.FileChooserView;
import moodle.sync.core.view.View;
import moodle.sync.javafx.core.util.FxUtils;

public class FxFileChooserView implements FileChooserView {

    private enum Type { OPEN, SAVE }

    private final Lock lock;

    private final Condition condition;

    private final FileChooser fileChooser;


    /**
     *
     */
    public FxFileChooserView() {
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.fileChooser = new FileChooser();
    }

    @Override
    public void addExtensionFilter(String description, String... extensions) {
        List<String> list = Arrays.stream(extensions).map(s -> "*." + s)
                .collect(Collectors.toList());

        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter(description, list));
    }

    @Override
    public void setInitialDirectory(File directory) {
        fileChooser.setInitialDirectory(directory);
    }

    @Override
    public void setInitialFileName(String name) {
        fileChooser.setInitialFileName(name);
    }

    @Override
    public File showOpenFile(View parent) {
        return openFileChooser(parent, Type.OPEN);
    }

    @Override
    public File showSaveFile(View parent) {
        return openFileChooser(parent, Type.SAVE);
    }

    private File openFileChooser(View parent, Type type) {
        if (Platform.isFxApplicationThread()) {
            return openDialog(parent, type);
        }

        return openDialogBlocked(parent, type);
    }

    /**
     * Open the FileChooser in the FX Application Thread and wait until
     * the FileChooser is closed.
     *
     * @param parent The parent view.
     * @param type The dialog selection type.
     *
     * @return The selected file or {@code null} if aborted.
     */
    private File openDialog(View parent, Type type) {
        File selectedFile;
        Window ownerWindow = null;

        if (nonNull(parent)) {
            if (!Node.class.isAssignableFrom(parent.getClass())) {
                throw new IllegalArgumentException("View expected to be a JavaFX Node.");
            }

            Node nodeView = (Node) parent;
            ownerWindow = nodeView.getScene().getWindow();
        }

        selectedFile = switch (type) {
            case OPEN -> fileChooser.showOpenDialog(ownerWindow);
            case SAVE -> fileChooser.showSaveDialog(ownerWindow);
        };

        return selectedFile;
    }

    /**
     * Open the FileChooser in the FX Application Thread and wait until
     * the FileChooser is closed. The call of this method will block the
     * executing thread.
     *
     * @param parent The parent view.
     * @param type The dialog selection type.
     *
     * @return The selected file or {@code null} if aborted.
     */
    private File openDialogBlocked(View parent, Type type) {
        lock.lock();

        File selectedFile;

        RunnableFuture<File> fileChooserRunnable = new FileChooserRunnable(parent, type);

        try {
            FxUtils.invoke(fileChooserRunnable);

            condition.await();

            selectedFile = fileChooserRunnable.get();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        finally {
            lock.unlock();
        }

        return selectedFile;
    }

    /**
     * Resume operation on the current thread.
     */
    private void resume() {
        lock.lock();

        try {
            condition.signal();
        }
        finally {
            lock.unlock();
        }
    }



    private class FileChooserRunnable implements RunnableFuture<File> {

        private final View parent;

        private final Type type;

        private File file;


        FileChooserRunnable(View parent, Type type) {
            this.parent = parent;
            this.type = type;
            this.file = null;
        }

        @Override
        public void run() {
            file = openDialog(parent, type);

            resume();
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public File get() {
            return file;
        }

        @Override
        public File get(long timeout, TimeUnit unit) {
            return file;
        }
    }
}
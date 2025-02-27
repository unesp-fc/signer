package br.unesp.fc.signer.event;

import br.unesp.fc.signer.model.FileModel;
import java.time.Clock;
import org.springframework.context.ApplicationEvent;

public class FileSelectedEvent extends ApplicationEvent {

    public FileSelectedEvent(FileModel source) {
        super(source);
    }

    public FileSelectedEvent(FileModel source, Clock clock) {
        super(source, clock);
    }

    @Override
    public String toString() {
        return ((FileModel)source).getFile().getName();
    }

}

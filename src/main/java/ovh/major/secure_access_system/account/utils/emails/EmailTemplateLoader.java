package ovh.major.secure_access_system.account.utils.emails;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ovh.major.secure_access_system.exceptions_and_errors.TemplateNotFoundException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class EmailTemplateLoader {

    public String load(String templateName) throws TemplateNotFoundException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/" + templateName);
        if (inputStream == null) {
            throw new TemplateNotFoundException("TEMPLATE LOADER: Template names " + templateName + " not found!");
        }
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
    }
}

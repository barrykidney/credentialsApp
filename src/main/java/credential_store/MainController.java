package credential_store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller    // This means that this class is a Controller
@RequestMapping(path="/credentials") // This means URL's start with /credentials (after Application path)
public class MainController {
    @Autowired // This means to get the bean called ServiceCredentialsRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private ServiceCredentialsRepository serviceCredentialsRepository;

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping(path="/")
    public @ResponseBody Iterable<CredentialSummary> getAllServiceCredentials() {
        List<CredentialSummary> allCredentialSummaries = new ArrayList<CredentialSummary>();
        Iterable<ServiceCredentials> allCredentials = serviceCredentialsRepository.findAll();

        for (ServiceCredentials credential : allCredentials) {
            allCredentialSummaries.add(new CredentialSummary(credential.getId(), credential.getServiceName(),
                    credential.getDateLastModified(), credential.getActive()));
        }
        return allCredentialSummaries;
    }

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping(path="/{id}")
    public @ResponseBody ServiceCredentials getServiceCredentialsById(@PathVariable Integer id) {
        return serviceCredentialsRepository.findById(id).orElseThrow(() -> new CredentialsNotFoundException(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path="/")
    public @ResponseBody ServiceCredentials addNewServiceCredentials(@RequestBody ServiceCredentials newServiceCredentials) {
        // This returns a JSON or XML with the ServiceCredentials
        newServiceCredentials.setDateLastModified(Instant.now().toEpochMilli());
        return serviceCredentialsRepository.save(newServiceCredentials);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/{id}")
    public @ResponseBody ServiceCredentials deleteServiceCredentials(@PathVariable Integer id) {
        ServiceCredentials retCredential;
        Optional<ServiceCredentials> optCredential = serviceCredentialsRepository.findById(id);
        if (optCredential.isPresent()) {
            retCredential = optCredential.get();
            serviceCredentialsRepository.deleteById(retCredential.getId());
        } else {
            retCredential = new ServiceCredentials();
        }
        return retCredential;
    }
}

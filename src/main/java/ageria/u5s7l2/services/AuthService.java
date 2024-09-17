package ageria.u5s7l2.services;


import ageria.u5s7l2.dto.EmailDTO;
import ageria.u5s7l2.dto.EmployeeLoginDTO;
import ageria.u5s7l2.entities.Employee;
import ageria.u5s7l2.exception.UnauthorizedException;
import ageria.u5s7l2.security.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    JWTTools jwtTools;

    @Autowired
    PasswordEncoder bcrypt;

    @Autowired
    JavaMailSender javaMailSender;

    public String checkCredentialAndGenerateToken(EmployeeLoginDTO body) {
        Employee found = this.employeeService.findFromEmail(body.email());
        if (bcrypt.matches(body.password(), found.getPassword())) {
            return jwtTools.createToken(found);
        } else {
            throw new UnauthorizedException("CREDENTIAL ARE NOT VALID");
        }
    }

    public void sendEmail(EmailDTO email) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("alessio@whimsykistchen.com"); // si puo passare come parametro
        msg.setSubject("MAIL FROM SITE");
        msg.setText("Email da: " + email.name() +
                " Azienda: " + email.azienda() + " email: " + email.email()
                + " telefono: " + email.number() + " Luogo Evento: " + email.luogo() +
                " Data Evento: " + email.data() + " Richiesta: " + email.richiesta());
    }

}

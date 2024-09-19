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
        msg.setTo("ageria20@gmail.com"); // si puo passare come parametro
        msg.setSubject("MAIL FROM SITE");
        String htmlMsg = "<html>" +
                "<p><strong>Email da:</strong> " + email.name() + "</p>" +
                "<p><strong>Azienda:</strong> " + email.azienda() + "</p>" +
                "<p><strong>Email:</strong> " + email.email() + "</p>" +
                "<p><strong>Telefono:</strong> " + email.number() + "</p>" +
                "<p><strong>Luogo Evento:</strong> " + email.luogo() + "</p>" +
                "<p><strong>Data Evento:</strong> " + email.data() + "</p>" +
                "<p><strong>Richiesta:</strong> " + email.richiesta() + "</p>" +
                "</html>";
        msg.setText(htmlMsg);
        javaMailSender.send(msg);
    }

}

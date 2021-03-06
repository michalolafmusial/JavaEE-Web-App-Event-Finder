package pl.findevent.servlets.usersservlets;

import pl.findevent.dao.UsersDao;
import pl.findevent.domain.User;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;

@WebServlet("/LoginServlet")
class LoginServlet extends HttpServlet {

    final Logger logger = Logger.getLogger(getClass().getName());

    @Inject
    UsersDao usersDao;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String redirect;
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        List<User> dbUser = usersDao
                .getUsersListFromDB()
                .stream()
                .filter(d -> d.getLogin().equals(login))
                .collect(toList());

        User loggedUser = dbUser.get(0);

        if (dbUser.isEmpty()) {
            logger.info("No such user: " + login);
            req.setAttribute("errorTitle", "Cannot login");
            req.setAttribute("errorDecscription", "No such user");
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        if (!dbUser.get(0).getActive()) {
            logger.info("No such user: " + login);
            req.setAttribute("errorTitle", "Cannot login");
            req.setAttribute("errorDecscription", "Inactive user");
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        if (dbUser.get(0).getLogin().equals(login) && dbUser.get(0).getPassword().equals(password)) {
            logger.info("User " + login + " logged-in successfully");
            req.getSession().setAttribute("login", login);
            req.getSession().setAttribute("id", loggedUser.getId());
            req.getSession().setAttribute("userType", loggedUser.getUserType());
            logger.info("Zalogowany user " + loggedUser.getUserType());
            redirect = "/index.jsp";
            RequestDispatcher rd = req.getRequestDispatcher(redirect);
            rd.forward(req, resp);
            return;

        } else {
            logger.info("Login failure for user: " + login);
            req.setAttribute("errorTitle", "Cannot login");
            req.setAttribute("errorDecscription", "Wrong username or password. Please try again");
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

    }
}
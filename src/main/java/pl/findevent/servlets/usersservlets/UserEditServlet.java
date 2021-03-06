package pl.findevent.servlets.usersservlets;

import pl.findevent.dao.UsersDao;
import pl.findevent.domain.User;
import pl.findevent.domain.UserType;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/UserEditServlet")
class UserEditServlet extends HttpServlet
{
    
    final Logger logger = Logger.getLogger(getClass().getName());
    
    @Inject
    UsersDao usersDao;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        req.setCharacterEncoding("UTF-8");
        String id = req.getParameter("id");
        req.setAttribute("id", id);
        req.getSession().setAttribute("id", id);
        
        User user = usersDao.read(Integer.parseInt(id));
        
        req.setAttribute("user", user);
        req.getSession().setAttribute("user", user);
        
        RequestDispatcher rd = req.getRequestDispatcher("editUser.jsp");
        rd.forward(req, resp);
        
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        
        req.setCharacterEncoding("UTF-8");
        
        HttpSession session = req.getSession();
        
        String idUser = String.valueOf(req.getSession().getAttribute("id"));
        
        String name = req.getParameter("name");
        String surname = req.getParameter("surname");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String type = req.getParameter("type");
        
        req.getSession().setAttribute("name", name);
        req.getSession().setAttribute("surname", surname);
        req.getSession().setAttribute("email", email);
        req.getSession().setAttribute("phone", phone);
        req.getSession().setAttribute("type", type);
        
        User user = usersDao.read(Integer.parseInt(idUser));
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setUserType(UserType.valueOf(type));
        
        usersDao.modifyUserDb(user);
        logger.info("User id: " + idUser + " successfully updated");
        logger.info("New name: " + name + " | New Surname: " + surname + " | New email: " + email + " | New Phone: " + phone
                    + " | New type: " + type);
        session.invalidate();
        
        RequestDispatcher rd = req.getRequestDispatcher("index.jsp");
        resp.sendRedirect(req.getContextPath() + "/ListAllUsersAdminServlet");
        rd.forward(req, resp);
        
    }
}
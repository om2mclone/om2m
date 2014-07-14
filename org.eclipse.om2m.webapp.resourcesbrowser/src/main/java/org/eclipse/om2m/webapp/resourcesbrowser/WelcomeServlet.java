/*******************************************************************************
 * Copyright (c) 2013-2014 LAAS-CNRS (www.laas.fr)
 * 7 Colonel Roche 31077 Toulouse - France
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Thierry Monteil (Project co-founder) - Management and initial specification,
 *         conception and documentation.
 *     Mahdi Ben Alaya (Project co-founder) - Management and initial specification,
 *         conception, implementation, test and documentation.
 *     Christophe Chassot - Management and initial specification.
 *     Khalil Drira - Management and initial specification.
 *     Yassine Banouar - Initial specification, conception, implementation, test
 *         and documentation.
 ******************************************************************************/
package org.eclipse.om2m.webapp.resourcesbrowser;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *  HTTP Servlet forwarding to the AJAX web interface index page.
 *  @author <ul>
 *            <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class WelcomeServlet extends HttpServlet {
    /** Serial Version UID */
    private static final long serialVersionUID = 1L;
      @Override
      protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
          //httpServletRequest.getRequestDispatcher("/webapps/index.html").forward (httpServletRequest, httpServletResponse);
          httpServletResponse.sendRedirect("/welcome/index.html?context="+System.getProperty("org.eclipse.om2m.sclBaseContext", "/om2m")+"&"+"sclId="+System.getProperty("org.eclipse.om2m.sclBaseId", "nscl"));
      }
}

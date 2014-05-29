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
package org.eclipse.om2m.commons.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.FrameworkUtil;
import org.xml.sax.SAXException;

/**
 * Validates resource XML representation using XSD files.
 *
 * @author <ul>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class XmlValidator {
    /** XmlValidator Logger */
    private static Log LOGGER = LogFactory.getLog(XmlValidator.class);
    /** XmlValidator Singleton */
    private static XmlValidator xmlValidator = new XmlValidator();
    /** Provides a factory API that enables applications to configure and obtain a SAX based parser to parse XML documents.*/
    private SAXParserFactory factory;
    /** Contains required XSD shemas to validate resource XML representation*/
    private Map<String, Schema> schemas;
    /** path to xsd files directory*/
    private String xsdPath="xsd";

    /** Gets XmlValidator instance */
    public static XmlValidator getInstance(){
        return xmlValidator;
    }

    /** Constructor */
    private XmlValidator(){
        schemas= new HashMap<String, Schema>();
        factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        // Create all required schemas based on xsd files when starting and add them to the schemas map to enhance response time performance.
        try {
            schemas.put("sclBase.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/sclBase.xsd")));
            schemas.put("accessRights.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/accessRights.xsd")));
            schemas.put("accessRight.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/accessRight.xsd")));
            schemas.put("accessRightAnnc.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/accessRightAnnc.xsd")));
            schemas.put("scls.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/scls.xsd")));
            schemas.put("scl.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/scl.xsd")));
            schemas.put("applications.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/applications.xsd")));
            schemas.put("application.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/application.xsd")));
            schemas.put("applicationAnnc.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/applicationAnnc.xsd")));
            schemas.put("containers.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/containers.xsd")));
            schemas.put("container.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/container.xsd")));
            schemas.put("containerAnnc.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/containerAnnc.xsd")));
            schemas.put("contentInstance.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/contentInstance.xsd")));
            schemas.put("subscription.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/subscription.xsd")));
            schemas.put("groups.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/groups.xsd")));
            schemas.put("group.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/group.xsd")));
            schemas.put("groupAnnc.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/groupAnnc.xsd")));
            schemas.put("attachedDevices.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/attachedDevices.xsd")));
            schemas.put("mgmtObjs.xsd", schemaFactory.newSchema(FrameworkUtil.getBundle(XmlValidator.class).getResource(xsdPath+"/mgmtObjs.xsd")));
        } catch (SAXException e) {
            LOGGER.error("Error reading XSD shemas", e);
        }
    }

    /**
     * Validates the resource representation according to xsd file.
     * @param representation - resource representation
     * @param xsd - xsd file
     * @throws SAXException
     * @throws IOException
     */
    public void validate(String representaion, String xsd) throws SAXException, IOException{
            Validator validator = schemas.get(xsd).newValidator();
            validator.validate(new StreamSource(new StringReader(representaion)));
    }

}

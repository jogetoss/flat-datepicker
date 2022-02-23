package org.joget.sample;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.displaytag.util.LookupUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListBinder;
import org.joget.apps.datalist.model.DataListCollection;
import org.joget.apps.datalist.model.DataListFilter;
import org.joget.apps.datalist.model.DataListFilterQueryObject;
import org.joget.apps.datalist.model.DataListFilterType;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormBuilderPalette;
import org.joget.apps.form.model.FormBuilderPaletteElement;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormUtil;
import org.joget.apps.userview.model.PwaOfflineResources;
import org.joget.commons.util.DateUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.ResourceBundleUtil;
import org.joget.commons.util.TimeZoneUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONArray;
import org.json.JSONObject;
//import org.springframework.context.i18n.LocaleContextHolder;

public class FlatDatePicker extends Element implements FormBuilderPaletteElement, PwaOfflineResources {
    
    public static final String RANGE_SEPARATOR = " to ";
    
    @Override
    public String getName() {
        return "Flat Date Picker";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "Flat Date Picker Element";
    }

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        String template = "flatDatePicker.ftl";
        
        String displayFormat = getPropertyString("format");
        String type = getPropertyString("datePickerType");
        String whiteBlackListing = getPropertyString("whiteBlackListing");
        
        
        // set value
        String value = FormUtil.getElementPropertyValue(this, formData);
        
        if (FormUtil.isReadonly(this, formData)) {
            if(type.equalsIgnoreCase("range")){
                String splitValues[] = value.split(RANGE_SEPARATOR);                
                String valueFrom = formattedDisplayValue(splitValues[0], displayFormat, formData);
                String valueTo = formattedDisplayValue(splitValues[1], displayFormat, formData);
                value = valueFrom + RANGE_SEPARATOR + valueTo;
            }else{
                value = formattedDisplayValue(value, displayFormat, formData);
            }
        } else {
            //value = formattedValue(value, displayFormat, formData);
            //dont need this because flatdatepicker will convert in the js script
        }
        
        // set value
        dataModel.put("value", value);
        
        //get white/blacklisting data
        if(!whiteBlackListing.equalsIgnoreCase("")){
            FormRowSet data = getData("", "");
            dataModel.put("whiteBlackListing", whiteBlackListing);
            dataModel.put("whiteBlackListingRows", data);
            
            String whiteBlackListingCustomDef = getPropertyString("whiteBlackListingCustomDef");
            if(whiteBlackListingCustomDef != null && !whiteBlackListingCustomDef.isEmpty()){
                dataModel.put("whiteBlackListingCustomDef", whiteBlackListingCustomDef);
            }
        }

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
        
        /*
        String template = "flatDatePicker.ftl";
        
        String displayFormat = getJavaDateFormat(getPropertyString("format"));
        String timeformat = getTimeFormat();
        if ("timeOnly".equalsIgnoreCase(getPropertyString("datePickerType"))) {
            displayFormat = timeformat;
        } else if ("dateTime".equalsIgnoreCase(getPropertyString("datePickerType"))) {
            displayFormat = displayFormat + " " + timeformat;
        }
        
        // set value
        String value = FormUtil.getElementPropertyValue(this, formData);
        
        if (FormUtil.isReadonly(this, formData)) {
            value = formattedDisplayValue(value, displayFormat, formData);
        } else {
            value = formattedValue(value, displayFormat, formData);
        }
        
        dataModel.put("displayFormat", displayFormat.toUpperCase());
        
        dataModel.put("value", value);

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;*/
    }
    
    public FormRowSet formatData(FormData formData) {
        //on save, execute this method
        //dont need to convert from display to data as the flat date picker has already converted it to data format
        FormRowSet rowSet = null;

        // get value
        String id = getPropertyString(FormUtil.PROPERTY_ID);
        String type = getPropertyString("datePickerType");
        String rangeSaveFields = getPropertyString("rangeSaveFields");
        if (id != null) {
            String value = FormUtil.getElementPropertyValue(this, formData);
            if (value != null) {
                // set value into Properties and FormRowSet object
                FormRow result = new FormRow();
                result.setProperty(id, value);
                
                if(type.equalsIgnoreCase("range") && rangeSaveFields.equalsIgnoreCase("true")){
                    String splitValues[] = value.split(RANGE_SEPARATOR);
                    result.setProperty(id + "_f", splitValues[0].trim());
                    result.setProperty(id + "_t", splitValues[1].trim());
                }
                            
                rowSet = new FormRowSet();
                rowSet.add(result);
            }
        }

        return rowSet;
        /*/
        FormRowSet rowSet = null;

        // get value
        String id = getPropertyString(FormUtil.PROPERTY_ID);
        if (id != null) {
            String value = FormUtil.getElementPropertyValue(this, formData);
            if (!FormUtil.isReadonly(this, formData) && getPropertyString("dataFormat") != null && !getPropertyString("dataFormat").isEmpty() 
                    && ("dateTime".equalsIgnoreCase(getPropertyString("datePickerType")) || getPropertyString("datePickerType").isEmpty())) {
                String binderValue = formData.getLoadBinderDataProperty(this, id);
                if (value != null && !value.equals(binderValue)) {
                    try {
                        String displayFormat = getJavaDateFormat(getPropertyString("format"));
                        if (!displayFormat.equals(getJavaDateFormat(getPropertyString("dataFormat")))) {

                            SimpleDateFormat data = new SimpleDateFormat(getPropertyString("dataFormat"));
                            SimpleDateFormat display = new SimpleDateFormat(displayFormat);
                            Date date = display.parse(value);
                            value = data.format(date);
                        }
                    } catch (Exception e) {}
                }
            }
            if (value != null) {
                // set value into Properties and FormRowSet object
                FormRow result = new FormRow();
                result.setProperty(id, value);
                rowSet = new FormRowSet();
                rowSet.add(result);
            }
        }

        return rowSet;
        */
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getFormBuilderTemplate() {
        return "<label class='label'>Flat Date Picker</label><input type='text' />";
    }

    @Override
    public String getLabel() {
        return "Flat Date Picker";
    }

    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/form/flatDatePicker.json", null, true, "message/form/flatDatePicker");
    }

    @Override
    public String getFormBuilderCategory() {
        return FormBuilderPalette.CATEGORY_CUSTOM;
    }

    @Override
    public int getFormBuilderPosition() {
        return 500;
    }

    @Override
    public String getFormBuilderIcon() {
        return "<i class=\"fas fa-calendar-alt\"></i>";
    }
    /*
    protected String getTimeFormat() {
        if ("timeOnly".equalsIgnoreCase(getPropertyString("datePickerType")) || "dateTime".equalsIgnoreCase(getPropertyString("datePickerType"))) {
            if ("true".equalsIgnoreCase(getPropertyString("format24hr"))) {
                return "HH:mm";
            } else {
                return "hh:mm a";
            }
        }
        return "";
    }
    */
    //TODO: need to handle time too
    protected String getJavaDateFormat(String format) {
        //variable format is display format
        
        if (format == null || format.isEmpty()) {
//            Locale locale = LocaleContextHolder.getLocale();
//            if (locale != null && locale.toString().startsWith("zh")) {
//                WorkflowUtil.getHttpServletRequest().setAttribute("currentLocale", locale);
                return "yyyy-MM-dd";
//            } else {
//                return "MM/dd/yyyy";
//            }
        }
        
        //TODO: disabled locale getLocate above due to error below
//        Caused by: java.lang.ClassNotFoundException: org.springframework.context.i18n.LocaleContextHolder not found by org.joget.sample.date-flatpickr [1]
//	at org.apache.felix.framework.BundleWiringImpl.findClassOrResourceByDelegation(BundleWiringImpl.java:1597)
//	at org.apache.felix.framework.BundleWiringImpl.access$300(BundleWiringImpl.java:79)
//	at org.apache.felix.framework.BundleWiringImpl$BundleClassLoader.loadClass(BundleWiringImpl.java:1982)
//	at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
        
        //DF: Day of the month, 2 digits with leading zeros	
        format = format.replaceAll("d", "dd");

        //DF: A textual representation of a day	Mon through Sun
        format = format.replaceAll("D", "EEE");

        //DF: A full textual representation of the day of the week
        format = format.replaceAll("l", "EEEE");
        
        //DF: Day of the month without leading zeros	1 to 31
        format = format.replaceAll("j", "d");
        
        //DF: A short textual representation of a month	
        format = format.replaceAll("M", "MMM");
        
        //DF: A full textual representation of a month
        format = format.replaceAll("F", "MMMMM");
        
        //DF: Numeric representation of a month, with leading zero
        format = format.replaceAll("m", "MM");
        
        //DF: Numeric representation of a month, without leading zeros	
        format = format.replaceAll("n", "M");

        //DF: A two digit representation of a year	
        format = format.replaceAll("y", "yy");
        
        //DF: A full numeric representation of a year, 4 digits	
        format = format.replaceAll("Y", "yyyy");
        
        //Time Formatting Tokens
        //G	Hours, 2 digits with leading zeros	1 to 12
        format = format.replaceAll("G", "hh");
        
        //DF: Hours 1 to 12
        format = format.replaceAll("h", "hh");
        
        //DF: Hours (24 hours)	00 to 23
        format = format.replaceAll("H", "HH");
        
        //i	Minutes	00 to 59
        format = format.replaceAll("i", "mm");
        
        //s	Seconds	0, 1 to 59
        format = format.replaceAll("s", "ss");
        
        //S	Seconds, 2 digits	00 to 59
        format = format.replaceAll("S", "ss");
        
        //K	AM/PM	AM or PM
        format = format.replaceAll("K", "a");
        
        
        
//        if (format.contains("DD")) {
//            format = format.replaceAll("DD", "EEEE");
//        } else {
//            format = format.replaceAll("D", "EEE");
//        }
//        
//        if (format.contains("MM")) {
//            format = format.replaceAll("MM", "MMMMM");
//        } else {
//            format = format.replaceAll("M", "MMM");
//        }
//        
//        if (format.contains("mm")) {
//            format = format.replaceAll("mm", "MM");
//        } else {
//            format = format.replaceAll("m", "M");
//        }
//        
//        if (format.contains("yy")) {
//            format = format.replaceAll("yy", "yyyy");
//        } else {
//            format = format.replaceAll("y", "yy");
//        }
//        
//        if (format.contains("tt") || format.contains("TT")) {
//            format = format.replaceAll("tt","a");
//            format = format.replaceAll("TT","a");
//        }
        
        return format;
    }
    /*
    @Override
    public Boolean selfValidate(FormData formData) {
        Boolean valid = true;
        String id = FormUtil.getElementParameterName(this);
        String value = FormUtil.getElementPropertyValue(this, formData);
               
        if (value != null && !value.isEmpty()) {
            String displayFormat = getJavaDateFormat(getPropertyString("format"));
            
            String timeformat = getTimeFormat();
            if ("timeOnly".equalsIgnoreCase(getPropertyString("datePickerType"))) {
                displayFormat = timeformat;
            } else if ("dateTime".equalsIgnoreCase(getPropertyString("datePickerType"))) {
                displayFormat = displayFormat + " " + timeformat;
            }
            
            String formattedValue = formattedValue(value, displayFormat, formData);
            valid = DateUtil.validateDateFormat(formattedValue, displayFormat);
            
            if (!valid) {
                formData.addFormError(id, ResourceBundleUtil.getMessage("form.datepicker.error.invalidFormat"));
            }
            
            Form form = null;
            if (!getPropertyString("startDateFieldId").isEmpty() ||
                !getPropertyString("endDateFieldId").isEmpty()) {
                form = FormUtil.findRootForm(this);
            }
            
            String startDate = "";
            String endDate = "";
            
            if (!getPropertyString("startDateFieldId").isEmpty()) {
                Element e = FormUtil.findElement(getPropertyString("startDateFieldId"), form, formData);
                if (e != null) {
                    String compareValue = FormUtil.getElementPropertyValue(e, formData);
                    if (compareValue != null && !compareValue.isEmpty()) {
                        String formattedCompare = compareValue;
                        if (e instanceof FlatDatePicker) {
                            formattedCompare = formatCompareValue(compareValue, displayFormat);
                        }
                        if (!DateUtil.compare(formattedCompare, formattedValue, displayFormat) && !formattedCompare.equals(value)) {
                            valid = false;
                            startDate = formattedCompare;
                        }
                    }
                }
            }
            
            if (!getPropertyString("endDateFieldId").isEmpty()) {
                Element e = FormUtil.findElement(getPropertyString("endDateFieldId"), form, formData);
                if (e != null) {
                    String compareValue = FormUtil.getElementPropertyValue(e, formData);
                    if (compareValue != null && !compareValue.isEmpty()) {
                        String formattedCompare = compareValue;
                        if (e instanceof FlatDatePicker) {
                            formattedCompare = formatCompareValue(compareValue, displayFormat);
                        }
                        if (!DateUtil.compare(formattedValue, formattedCompare , displayFormat) && !formattedCompare.equals(value)) {
                            valid = false;
                            endDate = formattedCompare;
                        }
                    }
                }
            }
            
            String type = getPropertyString("currentDateAs");
            if (!type.isEmpty()) {
                String formattedCompare = TimeZoneUtil.convertToTimeZone(new Date(), null, displayFormat);
                String start, end;
                if ("minDate".equals(type)) {
                    start = formattedCompare;
                    end = formattedValue;
                } else {
                    start = formattedValue;
                    end = formattedCompare;
                }
                
                if (!DateUtil.compare(start, end , displayFormat) && !formattedCompare.equals(formattedValue)) {
                    valid = false;
                    
                    if ("minDate".equals(type)) {
                        if (startDate.isEmpty() || !DateUtil.compare(formattedCompare, startDate, displayFormat)) {
                            startDate = formattedCompare;
                        }
                    } else {
                        if (endDate.isEmpty() || !DateUtil.compare(endDate, formattedCompare, displayFormat)) {
                            endDate = formattedCompare;
                        }
                    }
                }
            }
                
            if (!startDate.isEmpty()) {
                formData.addFormError(id, ResourceBundleUtil.getMessage("form.datepicker.error.minDate", new String[]{startDate}));
            }

            if (!endDate.isEmpty()) {
                formData.addFormError(id, ResourceBundleUtil.getMessage("form.datepicker.error.maxDate", new String[]{endDate}));
            }
        }
        
        return valid;
    }
    
    private String formatCompareValue(String value, String displayFormat) {
        String dataFormat = getPropertyString("dataFormat");
        
        String tempValue = value.replaceAll("[0-9]", "x");
        String tempFormat = dataFormat.replaceAll("[a-zA-Z]", "x");
            
        if (!displayFormat.equals(dataFormat) && tempValue.equals(tempFormat)) {
            try {
                SimpleDateFormat data = new SimpleDateFormat(getJavaDateFormat(dataFormat));
                SimpleDateFormat display = new SimpleDateFormat(getJavaDateFormat(displayFormat));
                Date date = data.parse(value);
                value = display.format(date);
            } catch (Exception e) {}
        }
        return value;
    }
    */
    private String formattedDisplayValue(String value, String displayFormat, FormData formData) {
        if (getPropertyString("dataFormat") != null && !getPropertyString("dataFormat").isEmpty()) {
            try {
                String dataFormat = getPropertyString("dataFormat");
                    
                if (!displayFormat.equals(dataFormat)) {
                    SimpleDateFormat data = new SimpleDateFormat(getJavaDateFormat(dataFormat));
                    SimpleDateFormat display = new SimpleDateFormat(getJavaDateFormat(displayFormat));
                    Date date = data.parse(value);
                    value = display.format(date);
                }
            } catch (Exception e) {
            }
        }
        return value;
    }
    
//    private String formattedValue(String value, String displayFormat, FormData formData) {
//        if (!FormUtil.isFormSubmitted(this, formData)) {
//            value = formattedDisplayValue(value, displayFormat, formData);
//        }
//        return value;
//    }
    
    
    protected String getObjectPropertyValue(Object r, String property) {
        String value = "";
        if(r instanceof FormRow){
            //Form Data Binder will return data containing FormRow
            FormRow fr = (FormRow) r;
            value = fr.getProperty(property);
        }else{
            //Advanced Form Data binder will return HashMap
            HashMap hm = ((HashMap) r);
            value = (String) hm.get(property);
        }
        
        return value;
    }
    
    protected FormRowSet getData(String startDate, String endDate) {
        FormRowSet dataArry = new FormRowSet();
        
        try {
            DataListCollection data = null;
            //String idColumn = getPropertyString("eventId");
            
            //get the binder
            Object binderData = getProperty("whiteBlackListingBinder");
            if (binderData != null && binderData instanceof Map) {
                Map bdMap = (Map) binderData;
                if (bdMap != null && bdMap.containsKey("className") && !bdMap.get("className").toString().isEmpty()) {
                    PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
                    DataListBinder binder = (DataListBinder) pluginManager.getPlugin(bdMap.get("className").toString());
                    
                    if (binder != null) {
                        Map bdProps = (Map) bdMap.get("properties");
                        binder.setProperties(bdProps);
                        
                        DataListFilterQueryObject[] filters = new DataListFilterQueryObject[0];
                        
//                        if ("true".equals(getPropertyString("autoHandleDateRange"))) {
//                            DataListFilterType filter = (DataListFilterType) pluginManager.getPlugin("org.joget.plugin.enterprise.DateRangeDataListFilterType");
//                            if (filter != null) {
//                                filter.setProperty("fromDefaultValue", startDate);
//                                filter.setProperty("toDefaultValue", endDate);
//                                filter.setProperty("format", "yy-mm-dd");
//                                filter.setProperty("formatJava", getPropertyString("dateFormat"));
//                                DataList datalist = new DataList();
//                                datalist.setBinder(binder);
//                                DataListFilterQueryObject filterObj = filter.getQueryObject(datalist, getPropertyString("fromDate"));
//                                filterObj.setOperator(DataListFilter.OPERATOR_AND);
//                                filters = new DataListFilterQueryObject[]{filterObj};
//                            }
//                        }
                        
                        data = binder.getData(null, bdProps, filters, null, null, null, null);
                        //if (idColumn.isEmpty()) {
                        //    idColumn = binder.getPrimaryKeyColumnName();
                        //}
                    }
                }
            }
            
            String dateFormat = getJavaDateFormat(getPropertyString("dateFormat"));
            String whiteBlackListingBinderDataFormat = getJavaDateFormat(getPropertyString("whiteBlackListingBinderDataFormat"));
//            String dateTimeFormat = getPropertyString("dateFormat");
//            if ("12hr".equals(getPropertyString("timeFormat"))) {
//                dateTimeFormat += " hh:mm a";
//            } else {
//                dateTimeFormat += " HH:mm";
//            }
            
            SimpleDateFormat sdf = new SimpleDateFormat(whiteBlackListingBinderDataFormat);
            SimpleDateFormat ndf = new SimpleDateFormat(dateFormat);
//            SimpleDateFormat sdtf = new SimpleDateFormat(dateTimeFormat);
//            SimpleDateFormat ndtf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String toDateCol = getPropertyString("whiteBlackListingBinderTo");
            String fromDateCol = getPropertyString("whiteBlackListingBinderFrom");
            
            
            if (data != null && !data.isEmpty()) {
                for (Object r : data) {
                    FormRow row = new FormRow();
                    String fromDate = "", toDate = "";
                    
                    if(toDateCol.equalsIgnoreCase(fromDateCol)){
                        //field value that contains a range, from and to in one field
                        String fromAndToDate = getObjectPropertyValue(r, fromDateCol);
                        String[] fromAndToDateArray = fromAndToDate.split(RANGE_SEPARATOR);
                        fromDate = fromAndToDateArray[0].trim();
                        toDate = fromAndToDateArray[1].trim();
                    }else{
                        //2 distinctive fields
                        if (!fromDateCol.isEmpty()) {
                            fromDate = getObjectPropertyValue(r, fromDateCol);
                        }
                        if (!toDateCol.isEmpty()) {
                              toDate = getObjectPropertyValue(r, toDateCol);
                        }
                    }
                    
                    if (fromDate != null && !fromDate.isEmpty()) {
                        fromDate = ndf.format(sdf.parse(fromDate));
                    }
                            
                    if (toDate != null && !toDate.isEmpty()) {
                        toDate = ndf.format(sdf.parse(toDate));
                    }

                    if (toDate != null && !toDate.isEmpty() && fromDate != null && !fromDate.isEmpty()) {
                        row.put("to", toDate);
                        row.put("from", fromDate);
                        dataArry.add(row);
                    }else if(fromDate != null && !fromDate.isEmpty()){
                        row.put("from", "");
                        row.put("date", fromDate);
                        dataArry.add(row);
                    }
                    
                }
            }
            
        } catch (Exception e) {
            LogUtil.error(FlatDatePicker.class.getName(), e, "");
        }
        
        return dataArry;
    }
    
//    protected String getValue(Object o, String name) {
//        String value = "";
//        
//        try {
//            Object v = LookupUtil.getBeanProperty(o, name);
//            if (v != null) {
//                return v.toString();
//            }
//        } catch (Exception e) {
//            LogUtil.error(FlatDatePicker.class.getName(), e, name);
//        }
//        
//        return value;
//    }
    
    @Override
    public Set<String> getOfflineStaticResources() {
        Set<String> urls = new HashSet<String>();
        String contextPath = AppUtil.getRequestContextPath();
        urls.add(contextPath + "/plugin/org.joget.sample.FlatDatePicker/css/flatpickr.min.css");
        urls.add(contextPath + "/plugin/org.joget.sample.FlatDatePicker/js/flatpickr.js");
        
        return urls;
    }
}

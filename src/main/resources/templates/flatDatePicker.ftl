<div class="form-cell" ${elementMetaData!}>
<#if element.properties.readonly! != 'true'>
    <link rel="stylesheet" href="${request.contextPath}/plugin/org.joget.sample.FlatDatePicker/css/flatpickr.min.css"/>
    <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.sample.FlatDatePicker/js/flatpickr.js"></script>
    
    <#if element.properties.theme! != ''>
    <link rel="stylesheet" href="${request.contextPath}/plugin/org.joget.sample.FlatDatePicker/css/themes/${element.properties.theme}"/>
    </#if>

    <script type="text/javascript">
        $(function($) {
            if($.isFunction($.fn.flatpickr)){
                $("#${elementParamName!}_${element.properties.elementUniqueKey!}").flatpickr({
                    
                    mode : "${element.properties.datePickerType}"
                    ,altInput: true
                    <#if element.properties.dataFormat! != ''>
                    ,dateFormat: "${element.properties.dataFormat}"
                    </#if>
                    <#if element.properties.format! != ''>
                    ,altFormat: "${element.properties.format}"
                    </#if>
                    <#if element.properties.datePickerTime! == 'dateTime'>
                    ,enableTime: true
                    </#if>
                    <#if element.properties.datePickerTime! == 'timeOnly'>
                    ,noCalendar: true
                    ,enableTime: true
                    </#if>
                    <#if element.properties.minDate! != ''>
                    ,minDate: "${element.properties.minDate}"
                    </#if>
                    <#if element.properties.maxDate! != ''>
                    ,maxDate: "${element.properties.maxDate}"
                    </#if>
                    <#if element.properties.format24hr! != ''>
                    ,time_24hr: true
                    </#if>
                    
                    <#if element.properties.whiteBlackListing! != ''>
                    ,${element.properties.whiteBlackListing}: [
                            ""
                        <#list whiteBlackListingRows as row>
                            <#if row['from'] != ''>
                            ,{
                                from: "${row['from']!?html}"
                                ,to: "${row['to']!?html}"
                            }
                            <#else>
                            , "${row['date']!?html}"
                            </#if>
                        </#list>
                        
                        <#if element.properties.whiteBlackListingCustomDef! != ''>
                        , ${element.properties.whiteBlackListingCustomDef}
                        </#if>
                        
                        ]
                    </#if>
                });
            }
        });
    </script>
</#if>
    <label class="label">${element.properties.label} <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
    <#if (element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
        <div id="${elementParamName!}_form-cell-value" class="form-cell-value"><span>${value!?html}</span></div>
        <input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" />
    <#else>
        <input id="${elementParamName!}_${element.properties.elementUniqueKey!}" name="${elementParamName!}" type="text" value="${value!?html}" placeholder="<#if (element.properties.placeholder! != '')>${element.properties.placeholder}<#else>${displayFormat!?html}</#if>" <#if error??>class="form-error-cell"</#if> <#if element.properties.readonly! == 'true'>readonly</#if> size="${element.properties.size!}" />
    </#if>
</div>
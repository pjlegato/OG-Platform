/*
 * Copyright 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * Please see distribution for license.
 */
$.register_module({
    name: 'og.analytics.form.ViewDefinitions',
    dependencies: ['og.common.util.ui.Dropdown'],
    obj: function () {
        var Block = og.common.util.ui.Block;
        var ViewDefinitions = function (config) {
            var block = this, form = config.form, index = 'viewdefinition', title = 'calculations';
            form.Block.call(block, {
                template: '<div class="og-option-title">' +
                    '<header class="OG-background-05">' + title + ':</header>'+
                    '{{{children}}}'+
                    '<div class="OG-icon og-icon-down"></div></div>',
                children: [new og.common.util.ui.Dropdown({
                    form: form, index: index, resource: 'viewdefinitions', fields: ['id', 'name'],
                    style: 'width: 100%', value: config.val, placeholder: 'Select...',
                    rest_options: {page: '*'}
                })],
                processor: function (data) {
                    if (!data[index]) // hack to get the value of a searchable dropdown
                        data[index] = $('#' + form.id + ' select[name=' + index + ']').val();
                }
            });
            block.on('form:load', function () {
                var selectedIndex,
                    select = $('#' + form.id + ' select[name=' + index + ']').searchable().hide(),
                    list = select.siblings('select').addClass('dropdown-list'),
                    input = select.siblings('input').attr('placeholder', 'Select..').select(), // Temp
                    toggle = select.parent().siblings('.og-icon-down');

                if (select.val() !== '') input.val(select.find("option:selected").text());

                input.removeAttr('style').blur(function () {
                    list.hide();
                }).keydown(function (event) {
                    if (event.keyCode === $.ui.keyCode.ESCAPE) list.hide();
                    if (event.keyCode === $.ui.keyCode.UP || event.keyCode === $.ui.keyCode.DOWN){
                        if (input.val() === '') input.focus(0).click();
                        list.show();
                    }
                }).click(function (event) {
                    list.show().prop('selectedIndex', selectedIndex);
                    select.prop('selectedIndex', selectedIndex);
                });

                toggle.click(function (event) {
                    selectedIndex = list.prop('selectedIndex');
                    if (!selectedIndex) {
                        select.prop('selectedIndex', selectedIndex);
                        list.show().prop('selectedIndex', selectedIndex);
                    }
                    input.focus(0).click();
                });

                list.on('mousedown', function (event) {
                    selectedIndex = list.get(0).selectedIndex;
                    select.get(0).selectedIndex = selectedIndex+1;
                    input.val(list.find("option:selected").text()).focus(0);
                });
            });
        };
        ViewDefinitions.prototype = new Block;
        return ViewDefinitions;
    }
});
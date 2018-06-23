/* 
 * Copyright 2016 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
//HaBM2 - datetimepicker
$(function () {
    $('#formCampaign').on('click', '#formCampaign\\:picker', function () {
        $('#formCampaign\\:picker').datetimepicker({
            format: 'DD/MM/YYYY HH:mm',
            inline: true,
            sideBySide: true
        });
    });
});


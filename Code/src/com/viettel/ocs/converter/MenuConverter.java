/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viettel.ocs.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

import com.viettel.ocs.entity.SysMenu;

@FacesConverter("menuConverter")
public class MenuConverter implements Converter {

	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {

		Object ret = null;
		if (uic instanceof PickList) {

			Object dualList = ((PickList) uic).getValue();
			DualListModel<SysMenu> dl = (DualListModel<SysMenu>) dualList;
			for (Object o : dl.getSource()) {

				SysMenu objMenu = (SysMenu) o;
				if (value.equals(String.valueOf(objMenu.getId()))) {
					ret = o;
					break;
				}
			}

			if (ret == null) {
				for (Object o : dl.getTarget()) {

					SysMenu objMenu = (SysMenu) o;
					if (value.equals(String.valueOf(objMenu.getId()))) {
						ret = o;
						break;
					}
				}
			}
		}

		return ret;
	}

	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object != null) {
			return String.valueOf(((SysMenu) object).getId());
		} else {
			return null;
		}
	}
}

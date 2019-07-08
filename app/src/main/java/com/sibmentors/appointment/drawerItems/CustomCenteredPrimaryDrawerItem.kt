package com.sibmentors.appointment.drawerItems

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.sibmentors.appointment.R

class CustomCenteredPrimaryDrawerItem : PrimaryDrawerItem() {

    override val layoutRes: Int
        get() = R.layout.material_drawer_item_primary_centered

    override val type: Int
        get() = R.id.material_drawer_item_centered_primary

}

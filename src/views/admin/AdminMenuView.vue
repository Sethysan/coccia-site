<template>
    <section>

        <input ref="photoInput" type="file" accept="image/*" hidden @change="handleMenuItemPhotoSelected">

        <header class="page-header">
            <div>
                <h1>Menu</h1>

                <p class="admin-subtext">
                    Manage menu sections, dishes, prices and visibility.
                </p>
            </div>
        </header>

        <section class="quick-find">
            <label for="menu-quick-find">
                Quick Find
            </label>

            <input id="menu-quick-find" v-model="quickFindQuery" type="search"
                placeholder="Search menu, recipes or subsections..." autocomplete="off">

            <div v-if="quickFindQuery.trim()" class="quick-find-results">
                <p v-if="quickFindResults.length === 0" class="state-message">
                    No matches found.
                </p>

                <button v-for="result in quickFindResults" v-else :key="`${result.type}-${result.id}`" type="button"
                    class="quick-find-result" @click="handleQuickFindResult(result)">
                    <span class="quick-find-result__name">
                        {{ result.name }}
                    </span>

                    <span class="quick-find-result__context">
                        {{
                            result.type === 'section'
                                ? 'Section'
                                : result.type === 'subsection'
                                    ? `Subsection · ${result.context}`
                                    : `Menu Item · ${result.context}`
                        }}
                    </span>
                </button>
            </div>
        </section>

        <section v-if="showCreateForm" class="admin-card create-card">
            <div class="admin-section-heading">
                <h2>Add Menu Section</h2>
            </div>

            <form class="admin-form create-form" @submit.prevent="handleCreateSection">
                <label>
                    Section name

                    <input v-model="newSection.name" type="text" maxlength="100" placeholder="Example: Starters"
                        required>
                </label>

                <label>
                    Included with this section

                    <textarea v-model="newSection.subtitle" rows="2"
                        placeholder="Example: Served with small dinner salad, choice of side & homemade bread & butter"></textarea>
                </label>

                <label>
                    Additional note

                    <textarea v-model="newSection.footerText" rows="2"
                        placeholder="Example: Ask about available dressings."></textarea>
                </label>

                <label>
                    Display order

                    <input v-model.number="newSection.displayOrder" type="number" min="0" required>
                </label>

                <div class="admin-form-actions">
                    <button type="submit" class="primary-button" :disabled="creatingSection">
                        {{
                            creatingSection
                                ? 'Adding...'
                                : 'Add Section'
                        }}
                    </button>

                    <button type="button" :disabled="creatingSection" @click="cancelCreateSection">
                        Cancel
                    </button>
                </div>
            </form>
        </section>

        <div v-if="menuStore.error" class="error-message" role="alert">
            {{ menuStore.error }}
        </div>

        <p v-if="menuStore.loading" class="state-message">
            Loading menu...
        </p>

        <p v-else-if="menuStore.sections.length === 0" class="state-message">
            No menu sections yet.
        </p>

        <section v-else class="section-list" :class="{
            'section-list--grid': !expandedSectionId,
            'section-list--workspace': expandedSectionId
        }">

            <!-- COLLAPSED GRID -->

            <Transition name="section-expand" mode="out-in" @after-enter="handleWorkspaceEntered">

                <!-- GRID VIEW -->

                <div v-if="!expandedSectionId" key="section-grid" class="section-grid-view">
                    <button v-for="section in menuStore.sections" :key="section.id" type="button" class="section-tile"
                        :class="{ inactive: !section.active }" @click="openSectionWorkspace(section)">
                        <span class="section-tile__name">
                            {{ section.name }}
                        </span>

                        <span v-if="!section.active" class="section-tile__status">
                            Inactive
                        </span>
                    </button>

                    <button type="button" class="section-tile section-tile--add"
                        @click="showCreateForm = !showCreateForm">
                        <span class="section-tile__name">
                            {{ showCreateForm ? 'Cancel' : '+ Add Section' }}
                        </span>
                    </button>
                </div>

                <!-- WORKSPACE VIEW -->

                <article v-else-if="expandedSection" :key="expandedSection.id"
                    class="admin-card section-card section-card--workspace"
                    :class="{ inactive: !expandedSection.active }">

                    <div class="workspace-header">
                        <div>
                            <span class="workspace-eyebrow">
                                Menu Section
                            </span>

                            <h2>{{ expandedSection.name }}</h2>
                        </div>

                        <button type="button" class="workspace-collapse-button" @click="collapseSectionWorkspace">
                            Collapse Section
                        </button>
                    </div>
                    <template v-for="section in [expandedSection]" :key="section.id">

                        <!-- EDIT MODE -->
                        <form v-if="editingSectionId === section.id" class="admin-form section-edit-form"
                            @submit.prevent="handleSaveSection(section.id)">
                            <div class="admin-section-heading">
                                <h2>Edit Section</h2>
                            </div>

                            <label>
                                Section name

                                <input v-model="editSection.name" type="text" maxlength="100" required>
                            </label>

                            <label>
                                Included with this section

                                <textarea v-model="editSection.subtitle" rows="2"></textarea>
                            </label>

                            <label>
                                Additional note

                                <textarea v-model="editSection.footerText" rows="2"></textarea>
                            </label>

                            <label>
                                Display order

                                <input v-model.number="editSection.displayOrder" type="number" min="0" required>
                            </label>

                            <div class="admin-form-actions">
                                <button type="submit" class="primary-button" :disabled="savingSectionId === section.id">
                                    {{
                                        savingSectionId === section.id
                                            ? 'Saving...'
                                            : 'Save Changes'
                                    }}
                                </button>

                                <button type="button" :disabled="savingSectionId === section.id"
                                    @click="cancelEditingSection">
                                    Cancel
                                </button>
                            </div>
                        </form>


                        <!-- NORMAL CARD -->

                        <template v-else>
                            <div class="section-heading">
                                <p v-if="section.subtitle" class="section-subtitle">
                                    {{ section.subtitle }}
                                </p>

                                <span class="status-badge" :class="section.active
                                    ? 'status-current'
                                    : 'status-archived'
                                    ">
                                    {{ section.active ? 'Active' : 'Inactive' }}
                                </span>
                            </div>

                            <p v-if="section.footerText" class="section-footer">
                                {{ section.footerText }}
                            </p>

                            <div class="section-meta">
                                Display order: {{ section.displayOrder }}
                            </div>

                            <div class="section-actions">

                                <button type="button" @click="startEditingSection(section)">
                                    Edit Section
                                </button>

                                <button type="button" :disabled="savingSectionId === section.id"
                                    @click="toggleSectionActive(section)">
                                    {{
                                        section.active
                                            ? 'Deactivate'
                                            : 'Reactivate'
                                    }}
                                </button>
                            </div>
                        </template>

                        <!-- SELECTED SECTION ACTIVE EDIT -->

                        <section v-if="
                            openSectionId === section.id
                            && editingSectionId !== section.id
                            && managingSubsections
                        " class="subsection-panel">
                            <div class="admin-section-heading">
                                <div>
                                    <h3>Subsections</h3>

                                    <span class="subsection-help">
                                        Optional groups within this section
                                    </span>
                                </div>

                                <button v-if="!showSubsectionForm" type="button" @click="startAddingSubsection">
                                    + Add Subsection
                                </button>
                            </div>

                            <form v-if="showSubsectionForm" id="menu-subsection-editor"
                                class="admin-form subsection-form" @submit.prevent="handleSaveSubsection(section.id)">
                                <label>
                                    Subsection name

                                    <input v-model="subsectionForm.name" type="text" maxlength="100"
                                        placeholder="Example: Beer" required>
                                </label>

                                <label>
                                    Shared price

                                    <input v-model="subsectionForm.price" type="number" min="0.01" step="0.01"
                                        placeholder="Optional, e.g. 3.00">

                                    <span class="subsection-help">
                                        Leave blank when items have individual prices.
                                    </span>
                                </label>

                                <label class="admin-checkbox-label">
                                    <input v-model="subsectionForm.active" type="checkbox">

                                    Active
                                </label>

                                <div class="admin-form-actions">
                                    <button type="submit" class="primary-button" :disabled="savingSubsection">
                                        {{
                                            savingSubsection
                                                ? 'Saving...'
                                                : editingSubsectionId
                                                    ? 'Save Changes'
                                                    : 'Add Subsection'
                                        }}
                                    </button>

                                    <button type="button" :disabled="savingSubsection" @click="cancelSubsectionForm">
                                        Cancel
                                    </button>
                                </div>
                            </form>

                            <div v-if="
                                (menuStore.subsectionsBySection[section.id] ?? [])
                                    .length
                            " class="subsection-list">
                                <div v-for="subsection in
                                    menuStore.subsectionsBySection[section.id]" :key="subsection.id"
                                    class="subsection-chip" :class="{ inactive: !subsection.active }">
                                    <span>
                                        {{ subsection.name }}
                                        <span v-if="subsection.price != null" class="subsection-price">
                                            · ${{ formatPrice(subsection.price) }}
                                        </span>
                                    </span>

                                    <div class="subsection-chip-actions">
                                        <button type="button" @click="startEditingSubsection(subsection)">
                                            Edit
                                        </button>

                                        <button type="button" :disabled="savingSubsection" @click="
                                            toggleSubsectionActive(
                                                section.id,
                                                subsection
                                            )
                                            ">
                                            {{
                                                subsection.active
                                                    ? 'Deactivate'
                                                    : 'Reactivate'
                                            }}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </section>

                        <section v-if="
                            openSectionId === section.id
                            && editingSectionId !== section.id
                        " class="menu-items-panel">

                            <div class="admin-section-heading">
                                <div>
                                    <h3>Menu Items</h3>

                                    <span>
                                        {{
                                            (menuStore.itemsBySection[section.id] ?? []).length
                                        }}
                                        {{
                                            (menuStore.itemsBySection[section.id] ?? []).length === 1
                                                ? 'item'
                                                : 'items'
                                        }}
                                    </span>
                                </div>

                                <div class="menu-items-heading-actions">
                                    <button v-if="(menuStore.subsectionsBySection[section.id] ?? []).length"
                                        type="button" @click="managingSubsections = !managingSubsections">
                                        {{
                                            managingSubsections
                                                ? 'Done Managing'
                                                : 'Manage Subsections'
                                        }}
                                    </button>

                                    <button v-if="(menuStore.itemsBySection[section.id] ?? []).length > 1" type="button"
                                        @click="rearrangingMenuItems = !rearrangingMenuItems">
                                        {{
                                            rearrangingMenuItems
                                                ? 'Done Rearranging'
                                                : 'Rearrange'
                                        }}
                                    </button>

                                    <button v-if="addingItemSectionId !== section.id" type="button"
                                        class="primary-button" @click="startAddingMenuItem(section.id)">
                                        + Add to {{ section.name }}
                                    </button>
                                </div>
                            </div>

                            <div v-if="addingItemSectionId === section.id && !editingMenuItem"
                                class="menu-item-form-panel">
                                <MenuItemForm :item="editingMenuItem" :saving="savingMenuItem"
                                    :section-name="section.name"
                                    :default-display-order="(menuStore.itemsBySection[section.id] ?? []).length"
                                    :subsections="menuStore.subsectionsBySection[section.id] ?? []" @submit="
                                        payload =>
                                            handleSaveMenuItem(section.id, payload)
                                    " @cancel="cancelMenuItemForm" />
                            </div>

                            <p v-if="
                                (menuStore.itemsBySection[section.id] ?? []).length === 0
                            " class="state-message">
                                No items in this section yet.
                            </p>

                            <div v-else>
                                <!-- GROUPED ITEMS -->
                                <div v-if="groupedMenuItems.length" class="menu-subsection-groups">
                                    <section v-for="group in groupedMenuItems" :key="group.subsection.id"
                                        class="menu-subsection-group" :class="{ inactive: !group.subsection.active }">
                                        <div class="menu-subsection-group-header">
                                            <div>
                                                <h4>
                                                    {{ group.subsection.name }}

                                                    <span v-if="group.subsection.price != null"
                                                        class="subsection-price">
                                                        · ${{ formatPrice(group.subsection.price) }}
                                                    </span>
                                                </h4>

                                                <span class="subsection-help">
                                                    {{ group.items.length }}
                                                    {{
                                                        group.items.length === 1
                                                            ? 'item'
                                                            : 'items'
                                                    }}
                                                </span>
                                            </div>
                                        </div>

                                        <p v-if="group.items.length === 0"
                                            class="state-message subsection-empty-message">
                                            No items in this subsection yet.
                                        </p>

                                        <div v-else class="menu-item-list">
                                            <template v-for="item in group.items" :key="item.id">
                                                <article class="menu-item-row">
                                                    <div class="menu-item-summary">
                                                        <div class="menu-item-thumbnail">
                                                            <img v-if="item.imageUrl" :src="item.imageUrl"
                                                                :alt="item.imageAlt || item.recipeName">

                                                            <span v-else>
                                                                No Photo
                                                            </span>
                                                        </div>

                                                        <div class="menu-item-details">
                                                            <strong>{{ item.recipeName }}</strong>

                                                            <button type="button" class="menu-item-photo-action"
                                                                :disabled="uploadingPhotoRecipeId === item.recipeId"
                                                                @click="chooseMenuItemPhoto(item)">
                                                                {{
                                                                    uploadingPhotoRecipeId === item.recipeId
                                                                        ? 'Saving...'
                                                                        : item.imageUrl
                                                                            ? 'Replace Photo'
                                                                            : 'Needs Photo · Add Photo'
                                                                }}
                                                            </button>

                                                            <div v-if="photoUploadItem?.id === item.id"
                                                                class="menu-item-photo-editor">

                                                                <div class="recipe-photo-field">
                                                                    <span class="recipe-photo-label">
                                                                        Photo
                                                                    </span>

                                                            <button type="button" class="photo-picker-button"
                                                                @click="openPhotoFilePicker">
                                                                {{ item.imageUrl ? 'Choose Replacement' : '+ Choose Photo' }}
                                                            </button>

                                                                    <span class="photo-filename">
                                                                        {{
                                                                            selectedPhotoFile
                                                                                ? selectedPhotoFile.name
                                                                                : item.imageUrl
                                                                                    ? 'Keep current photo'
                                                                                    : 'No photo selected'
                                                                        }}
                                                                    </span>
                                                                </div>

                                                                <label>
                                                                    Image description

                                                                    <input v-model="photoImageAlt" type="text"
                                                                        maxlength="255"
                                                                        placeholder="Describe the photo for accessibility">
                                                                </label>

                                                                <label>
                                                                    Photo caption

                                                                    <input v-model="photoImageCaption" type="text"
                                                                        maxlength="255"
                                                                        placeholder="Example: Pictured with cannellini beans.">
                                                                </label>

                                                                <div class="admin-form-actions">
                                                                    <button type="button" class="primary-button"
                                                                        :disabled="uploadingPhotoRecipeId === item.recipeId
                                                                            || (!item.imageUrl && !selectedPhotoFile)
                                                                            " @click="saveMenuItemPhoto">
                                                                        {{
                                                                            uploadingPhotoRecipeId === item.recipeId
                                                                                ? 'Saving...'
                                                                                : 'Save Photo'
                                                                        }}
                                                                    </button>

                                                                    <button type="button"
                                                                        :disabled="uploadingPhotoRecipeId === item.recipeId"
                                                                        @click="cancelMenuItemPhoto">
                                                                        Cancel
                                                                    </button>
                                                                </div>
                                                            </div>

                                                            <p v-if="item.description">
                                                                {{ item.description }}
                                                            </p>

                                                            <ul class="menu-item-prices">
                                                                <li v-for="price in item.prices" :key="price.id">
                                                                    <span v-if="price.label">
                                                                        {{ price.label }}:
                                                                    </span>

                                                                    ${{ formatPrice(price.amount) }}
                                                                </li>
                                                            </ul>
                                                        </div>
                                                    </div>

                                                    <div class="menu-item-actions">
                                                        <span class="item-status" :class="{ hidden: !item.visible }">
                                                            {{ item.visible ? 'Visible' : 'Hidden' }}
                                                        </span>

                                                        <button
                                                            v-if="rearrangingMenuItems && group.items.indexOf(item) > 0"
                                                            type="button" :disabled="movingMenuItemId === item.id"
                                                            @click="
                                                                handleMoveMenuItem(
                                                                    section.id,
                                                                    item.id,
                                                                    'UP'
                                                                )
                                                                ">
                                                            ↑
                                                        </button>

                                                        <button v-if="
                                                            rearrangingMenuItems
                                                            && group.items.indexOf(item) < group.items.length - 1
                                                        " type="button" :disabled="movingMenuItemId === item.id"
                                                            @click="
                                                                handleMoveMenuItem(
                                                                    section.id,
                                                                    item.id,
                                                                    'DOWN'
                                                                )
                                                                ">
                                                            ↓
                                                        </button>

                                                        <button type="button" :disabled="savingMenuItem" @click="
                                                            toggleMenuItemVisibility(
                                                                section.id,
                                                                item
                                                            )
                                                            ">
                                                            {{ item.visible ? 'Hide' : 'Show' }}
                                                        </button>

                                                        <button type="button" :disabled="savingMenuItem" @click="
                                                            startEditingMenuItem(
                                                                section.id,
                                                                item
                                                            )
                                                            ">
                                                            Edit
                                                        </button>
                                                    </div>
                                                </article>
                                                <div v-if="editingMenuItem?.id === item.id"
                                                    :id="`menu-item-editor-${item.id}`"
                                                    class="menu-item-form-panel menu-item-form-panel--inline">
                                                    <MenuItemForm :item="editingMenuItem" :saving="savingMenuItem"
                                                        :section-name="section.name"
                                                        :default-display-order="item.displayOrder"
                                                        :subsections="menuStore.subsectionsBySection[section.id] ?? []"
                                                        @submit="
                                                            payload =>
                                                                handleSaveMenuItem(section.id, payload)
                                                        " @cancel="cancelMenuItemForm" />
                                                </div>
                                            </template>
                                        </div>
                                    </section>
                                </div>

                                <!-- ITEMS WITHOUT A SUBSECTION -->
                                <section v-if="ungroupedMenuItems.length" class="menu-subsection-group">
                                    <div v-if="groupedMenuItems.length" class="menu-subsection-group-header">
                                        <div>
                                            <h4> Other Items </h4>

                                            <span class="subsection-help">
                                                {{ ungroupedMenuItems.length }}
                                                {{
                                                    ungroupedMenuItems.length === 1
                                                        ? 'item'
                                                        : 'items'
                                                }}
                                            </span>
                                        </div>
                                    </div>

                                    <div class="menu-item-list">
                                        <template v-for="item in ungroupedMenuItems" :key="item.id">
                                            <article class="menu-item-row">
                                                <div class="menu-item-summary">
                                                    <div class="menu-item-thumbnail">
                                                        <img v-if="item.imageUrl" :src="item.imageUrl"
                                                            :alt="item.imageAlt || item.recipeName">

                                                        <span v-else>
                                                            No Photo
                                                        </span>
                                                    </div>

                                                    <div class="menu-item-details">
                                                        <strong>{{ item.recipeName }}</strong>

                                                        <button type="button" class="menu-item-photo-action"
                                                            :disabled="uploadingPhotoRecipeId === item.recipeId"
                                                            @click="chooseMenuItemPhoto(item)">
                                                            {{
                                                                uploadingPhotoRecipeId === item.recipeId
                                                                    ? 'Saving...'
                                                                    : item.imageUrl
                                                                        ? 'Replace Photo'
                                                                        : 'Needs Photo · Add Photo'
                                                            }}
                                                        </button>

                                                        <div v-if="photoUploadItem?.id === item.id"
                                                            class="menu-item-photo-editor">

                                                            <div class="recipe-photo-field">
                                                                <span class="recipe-photo-label">
                                                                    Photo
                                                                </span>

                                                        <button type="button" class="photo-picker-button"
                                                            @click="openPhotoFilePicker">
                                                            {{ item.imageUrl ? 'Choose Replacement' : '+ Choose Photo' }}
                                                        </button>

                                                                <span class="photo-filename">
                                                                    {{
                                                                        selectedPhotoFile
                                                                            ? selectedPhotoFile.name
                                                                            : item.imageUrl
                                                                                ? 'Keep current photo'
                                                                                : 'No photo selected'
                                                                    }}
                                                                </span>
                                                            </div>

                                                            <label>
                                                                Image description

                                                                <input v-model="photoImageAlt" type="text"
                                                                    maxlength="255"
                                                                    placeholder="Describe the photo for accessibility">
                                                            </label>

                                                            <label>
                                                                Photo caption

                                                                <input v-model="photoImageCaption" type="text"
                                                                    maxlength="255"
                                                                    placeholder="Example: Pictured with cannellini beans.">
                                                            </label>

                                                            <div class="admin-form-actions">
                                                                <button type="button" class="primary-button" :disabled="uploadingPhotoRecipeId === item.recipeId
                                                                    || (!item.imageUrl && !selectedPhotoFile)
                                                                    " @click="saveMenuItemPhoto">
                                                                    {{
                                                                        uploadingPhotoRecipeId === item.recipeId
                                                                            ? 'Saving...'
                                                                            : 'Save Photo'
                                                                    }}
                                                                </button>

                                                                <button type="button"
                                                                    :disabled="uploadingPhotoRecipeId === item.recipeId"
                                                                    @click="cancelMenuItemPhoto">
                                                                    Cancel
                                                                </button>
                                                            </div>
                                                        </div>

                                                        <p v-if="item.description">
                                                            {{ item.description }}
                                                        </p>

                                                        <ul class="menu-item-prices">
                                                            <li v-for="price in item.prices" :key="price.id">
                                                                <span v-if="price.label">
                                                                    {{ price.label }}:
                                                                </span>

                                                                ${{ formatPrice(price.amount) }}
                                                            </li>
                                                        </ul>
                                                    </div>
                                                </div>

                                                <div class="menu-item-actions">
                                                    <span class="item-status" :class="{ hidden: !item.visible }">
                                                        {{ item.visible ? 'Visible' : 'Hidden' }}
                                                    </span>

                                                    <button v-if="
                                                        rearrangingMenuItems
                                                        && ungroupedMenuItems.indexOf(item) > 0
                                                    " type="button" :disabled="movingMenuItemId === item.id" @click="
                                                        handleMoveMenuItem(
                                                            section.id,
                                                            item.id,
                                                            'UP'
                                                        )
                                                        ">
                                                        ↑
                                                    </button>

                                                    <button v-if="
                                                        rearrangingMenuItems
                                                        && ungroupedMenuItems.indexOf(item)
                                                        < ungroupedMenuItems.length - 1
                                                    " type="button" :disabled="movingMenuItemId === item.id" @click="
                                                        handleMoveMenuItem(
                                                            section.id,
                                                            item.id,
                                                            'DOWN'
                                                        )
                                                        ">
                                                        ↓
                                                    </button>

                                                    <button type="button" :disabled="savingMenuItem" @click="
                                                        toggleMenuItemVisibility(
                                                            section.id,
                                                            item
                                                        )
                                                        ">
                                                        {{ item.visible ? 'Hide' : 'Show' }}
                                                    </button>

                                                    <button type="button" :disabled="savingMenuItem" @click="
                                                        startEditingMenuItem(
                                                            section.id,
                                                            item
                                                        )
                                                        ">
                                                        Edit
                                                    </button>
                                                </div>
                                            </article>
                                            <div v-if="editingMenuItem?.id === item.id"
                                                :id="`menu-item-editor-${item.id}`"
                                                class="menu-item-form-panel menu-item-form-panel--inline">
                                                <MenuItemForm :item="editingMenuItem" :saving="savingMenuItem"
                                                    :section-name="section.name"
                                                    :default-display-order="item.displayOrder"
                                                    :subsections="menuStore.subsectionsBySection[section.id] ?? []"
                                                    @submit="
                                                        payload =>
                                                            handleSaveMenuItem(section.id, payload)
                                                    " @cancel="cancelMenuItemForm" />
                                            </div>
                                        </template>
                                    </div>
                                </section>
                            </div>
                        </section>

                        <!-- PIZZA CONFIGURATION -->

                        <PizzaManager v-if="
                            openPizzaSectionId === section.id
                            && editingSectionId !== section.id
                        " :section="section" />
                    </template>
                </article>
            </Transition>
        </section>
    </section>
</template>

<script setup>

import { computed, nextTick, onMounted, ref } from 'vue'

import { useMenuStore } from '@/stores/menuStore'
import {
    updateRecipeImageDetails,
    uploadRecipeImage
} from '@/api/recipesApi'
import MenuItemForm from '@/components/admin/MenuItemForm.vue'
import PizzaManager from '@/components/admin/PizzaManager.vue'

const menuStore = useMenuStore()

const quickFindQuery = ref('')

const pendingQuickFindTarget = ref(null)

const showCreateForm = ref(false)

const creatingSection = ref(false)

const editingSectionId = ref(null)
const savingSectionId = ref(null)

const openPizzaSectionId = ref(null)
const openSectionId = ref(null)

const expandedSectionId = ref(null)

const expandedSection = computed(() =>
    menuStore.sections.find(
        section => section.id === expandedSectionId.value
    ) ?? null
)

const quickFindResults = computed(() => {
    const query = quickFindQuery.value.trim().toLowerCase()

    if (!query) {
        return []
    }

    const results = []

    menuStore.sections.forEach(section => {
        if (section.name.toLowerCase().includes(query)) {
            results.push({
                type: 'section',
                id: section.id,
                name: section.name,
                context: 'Menu Section',
                section
            })
        }

        const subsections =
            menuStore.subsectionsBySection[section.id] ?? []

        subsections.forEach(subsection => {
            if (subsection.name.toLowerCase().includes(query)) {
                results.push({
                    type: 'subsection',
                    id: subsection.id,
                    name: subsection.name,
                    context: section.name,
                    section,
                    subsection
                })
            }
        })

        const items =
            menuStore.itemsBySection[section.id] ?? []

        items.forEach(item => {
            const searchableText = [
                item.recipeName,
                item.description,
                item.menuSubsectionName
            ]
                .filter(Boolean)
                .join(' ')
                .toLowerCase()

            if (searchableText.includes(query)) {
                results.push({
                    type: 'item',
                    id: item.id,
                    name: item.recipeName,
                    context: item.menuSubsectionName
                        ? `${section.name} › ${item.menuSubsectionName}`
                        : section.name,
                    section,
                    item
                })
            }
        })
    })

    return results
})

const addingItemSectionId = ref(null)
const editingMenuItem = ref(null)
const savingMenuItem = ref(false)
const movingMenuItemId = ref(null)

const photoInput = ref(null)
const photoUploadItem = ref(null)
const selectedPhotoFile = ref(null)
const photoImageAlt = ref('')
const photoImageCaption = ref('')
const uploadingPhotoRecipeId = ref(null)

const groupedMenuItems = computed(() => {
    if (!expandedSectionId.value) {
        return []
    }

    const subsections =
        menuStore.subsectionsBySection[expandedSectionId.value] ?? []

    const items =
        menuStore.itemsBySection[expandedSectionId.value] ?? []

    return subsections.map(subsection => ({
        subsection,
        items: items.filter(
            item =>
                Number(item.menuSubsectionId) ===
                Number(subsection.id)
        )
    }))
})

const ungroupedMenuItems = computed(() => {
    if (!expandedSectionId.value) {
        return []
    }

    const items =
        menuStore.itemsBySection[expandedSectionId.value] ?? []

    return items.filter(
        item => item.menuSubsectionId == null
    )
})

const managingSubsections = ref(false)
const rearrangingMenuItems = ref(false)
const showSubsectionForm = ref(false)
const editingSubsectionId = ref(null)
const savingSubsection = ref(false)

const subsectionForm = ref({
    name: '',
    price: null,
    active: true
})

const editSection = ref({
    name: '',
    subtitle: '',
    footerText: '',
    displayOrder: 0,
    active: true
})

const newSection = ref({
    name: '',
    subtitle: '',
    footerText: '',
    displayOrder: 0
})

onMounted(async () => {
    await menuStore.fetchSections()

    await Promise.all(
        menuStore.sections.map(section =>
            Promise.all([
                menuStore.fetchItems(section.id),
                menuStore.fetchSubsections(section.id)
            ])
        )
    )
})

function cancelCreateSection() {
    showCreateForm.value = false

    newSection.value = {
        name: '',
        subtitle: '',
        footerText: '',
        displayOrder: 0
    }

    menuStore.clearError()
}

function startEditingSection(section) {
    menuStore.clearError()

    editingSectionId.value = section.id

    editSection.value = {
        name: section.name,
        subtitle: section.subtitle ?? '',
        footerText: section.footerText ?? '',
        displayOrder: section.displayOrder,
        active: section.active
    }
}

function cancelEditingSection() {
    editingSectionId.value = null

    editSection.value = {
        name: '',
        subtitle: '',
        footerText: '',
        displayOrder: 0,
        active: true
    }

    menuStore.clearError()
}

async function handleSaveSection(sectionId) {
    const name = editSection.value.name.trim()

    if (!name) {
        return
    }

    savingSectionId.value = sectionId
    menuStore.clearError()

    try {
        const savedSection =
            await menuStore.saveSection(
                sectionId,
                {
                    name,
                    subtitle: editSection.value.subtitle,
                    footerText: editSection.value.footerText,
                    displayOrder:
                        editSection.value.displayOrder,
                    active: editSection.value.active
                }
            )

        if (savedSection) {
            cancelEditingSection()
        }

    } finally {
        savingSectionId.value = null
    }
}

async function toggleSectionActive(section) {
    const action = section.active
        ? 'deactivate'
        : 'reactivate'

    const confirmed = window.confirm(
        `Are you sure you want to ${action} ${section.name}?`
    )

    if (!confirmed) {
        return
    }

    savingSectionId.value = section.id
    menuStore.clearError()

    try {
        await menuStore.saveSection(
            section.id,
            {
                name: section.name,
                subtitle: section.subtitle,
                footerText: section.footerText,
                displayOrder: section.displayOrder,
                active: !section.active
            }
        )

    } finally {
        savingSectionId.value = null
    }
}

function startAddingSubsection() {
    editingSubsectionId.value = null

    subsectionForm.value = {
        name: '',
        price: null,
        active: true
    }

    showSubsectionForm.value = true
    menuStore.clearError()
}

function startEditingSubsection(subsection) {
    editingSubsectionId.value = subsection.id

    subsectionForm.value = {
        name: subsection.name,
        price: subsection.price ?? null,
        active: subsection.active
    }

    showSubsectionForm.value = true
    menuStore.clearError()
}

function cancelSubsectionForm() {
    showSubsectionForm.value = false
    editingSubsectionId.value = null

    subsectionForm.value = {
        name: '',
        price: null,
        active: true
    }

    menuStore.clearError()
}

async function handleSaveSubsection(sectionId) {
    const name = subsectionForm.value.name.trim()

    if (!name) {
        return
    }

    savingSubsection.value = true
    menuStore.clearError()

    try {
        const payload = {
            name,
            price:
                subsectionForm.value.price === null
                    || subsectionForm.value.price === ''
                    ? null
                    : Number(subsectionForm.value.price),
            active: subsectionForm.value.active
        }

        const savedSubsection =
            editingSubsectionId.value
                ? await menuStore.saveSubsection(
                    sectionId,
                    editingSubsectionId.value,
                    payload
                )
                : await menuStore.addSubsection(
                    sectionId,
                    payload
                )

        if (savedSubsection) {
            cancelSubsectionForm()
        }

    } finally {
        savingSubsection.value = false
    }
}

async function toggleSubsectionActive(
    sectionId,
    subsection
) {
    savingSubsection.value = true
    menuStore.clearError()

    try {
        await menuStore.saveSubsection(
            sectionId,
            subsection.id,
            {
                name: subsection.name,
                price: subsection.price ?? null,
                active: !subsection.active
            }
        )

    } finally {
        savingSubsection.value = false
    }
}

function startAddingMenuItem(sectionId) {
    editingMenuItem.value = null
    addingItemSectionId.value = sectionId

    menuStore.clearError()
}

function startEditingMenuItem(sectionId, item) {
    openSectionId.value = sectionId
    addingItemSectionId.value = sectionId
    editingMenuItem.value = item

    menuStore.clearError()
}

function cancelMenuItemForm() {
    addingItemSectionId.value = null
    editingMenuItem.value = null

    menuStore.clearError()
}

async function handleSaveMenuItem(sectionId, payload) {
    savingMenuItem.value = true
    menuStore.clearError()

    try {
        const savedItem = editingMenuItem.value
            ? await menuStore.saveItem(
                sectionId,
                editingMenuItem.value.id,
                payload
            )
            : await menuStore.addItem(
                sectionId,
                payload
            )

        if (savedItem) {
            cancelMenuItemForm()
        }

    } finally {
        savingMenuItem.value = false
    }
}

function chooseMenuItemPhoto(item) {
    photoUploadItem.value = item
    selectedPhotoFile.value = null
    photoImageAlt.value = item.imageAlt || ''
    photoImageCaption.value = item.imageCaption || ''

    if (photoInput.value) {
        photoInput.value.value = ''
    }

    menuStore.clearError()
}

function handleMenuItemPhotoSelected(event) {
    selectedPhotoFile.value =
        event.target.files?.[0] ?? null
}

function openPhotoFilePicker() {
    if (photoInput.value) {
        photoInput.value.value = ''
        photoInput.value.click()
    }
}

function cancelMenuItemPhoto() {
    photoUploadItem.value = null
    selectedPhotoFile.value = null
    photoImageAlt.value = ''
    photoImageCaption.value = ''

    if (photoInput.value) {
        photoInput.value.value = ''
    }

    menuStore.clearError()
}

async function saveMenuItemPhoto() {
    const item = photoUploadItem.value

    if (!item) {
        return
    }

    uploadingPhotoRecipeId.value = item.recipeId
    menuStore.clearError()

    try {
        await updateRecipeImageDetails(
    item.recipeId,
    {
        imageAlt: photoImageAlt.value,
        imageCaption: photoImageCaption.value
    }
)

        if (selectedPhotoFile.value) {
            await uploadRecipeImage(
                item.recipeId,
                selectedPhotoFile.value
            )
        }

        await menuStore.fetchItems(
            expandedSectionId.value
        )

        cancelMenuItemPhoto()

    } catch (error) {
        menuStore.error =
            error?.message
            || 'Unable to save recipe photo.'

    } finally {
        uploadingPhotoRecipeId.value = null
    }
}

async function handleMoveMenuItem(
    sectionId,
    menuItemId,
    direction
) {
    movingMenuItemId.value = menuItemId
    menuStore.clearError()

    try {
        await menuStore.moveItem(
            sectionId,
            menuItemId,
            direction
        )
    } finally {
        movingMenuItemId.value = null
    }
}

function formatPrice(amount) {
    return Number(amount).toFixed(2)
}

async function toggleMenuItemVisibility(sectionId, item) {
    savingMenuItem.value = true
    menuStore.clearError()

    try {
        await menuStore.saveItem(
            sectionId,
            item.id,
            {
                recipeId: item.recipeId,
                menuSubsectionId: item.menuSubsectionId ?? null,
                displayOrder: item.displayOrder,
                visible: !item.visible,

                prices: item.prices.map(
                    (price, index) => ({
                        label: price.label ?? null,
                        amount: Number(price.amount),
                        displayOrder: index
                    })
                )
            }
        )
    } finally {
        savingMenuItem.value = false
    }
}

async function handleCreateSection() {
    const name = newSection.value.name.trim()

    if (!name) {
        return
    }

    creatingSection.value = true
    menuStore.clearError()

    try {
        const createdSection =
            await menuStore.addSection({
                name,
                subtitle: newSection.value.subtitle,
                footerText: newSection.value.footerText,
                displayOrder:
                    newSection.value.displayOrder,
                active: true
            })

        if (createdSection) {
            cancelCreateSection()
        }

    } finally {
        creatingSection.value = false
    }
}

async function handleQuickFindResult(result) {
    quickFindQuery.value = ''

    pendingQuickFindTarget.value =
        result.type === 'section'
            ? null
            : result

    await openSectionWorkspace(result.section)

    if (result.type === 'section') {
        return
    }

    if (result.type === 'subsection') {
        managingSubsections.value = true
        startEditingSubsection(result.subsection)
        return
    }

    if (result.type === 'item') {
        startEditingMenuItem(
            result.section.id,
            result.item
        )
    }
}

async function handleWorkspaceEntered() {
    const target = pendingQuickFindTarget.value

    if (!target) {
        return
    }

    await nextTick()

    const element =
        target.type === 'subsection'
            ? document.getElementById(
                'menu-subsection-editor'
            )
            : document.getElementById(
                `menu-item-editor-${target.item.id}`
            )

    if (!element) {
        return
    }

    element.scrollIntoView({
        behavior: 'smooth',
        block: 'center'
    })

    pendingQuickFindTarget.value = null
}

async function openSectionWorkspace(section) {
    expandedSectionId.value = section.id

    editingSectionId.value = null
    addingItemSectionId.value = null
    editingMenuItem.value = null

    managingSubsections.value = false
    showSubsectionForm.value = false
    editingSubsectionId.value = null

    subsectionForm.value = {
        name: '',
        price: null,
        active: true
    }

    const isPizza =
        section.name.trim().toLowerCase() === 'pizza'

    if (isPizza) {
        openPizzaSectionId.value = section.id
        openSectionId.value = null

        return
    }

    openPizzaSectionId.value = null
    openSectionId.value = section.id

    await Promise.all([
        menuStore.fetchItems(section.id),
        menuStore.fetchSubsections(section.id)
    ])
}

function collapseSectionWorkspace() {
    expandedSectionId.value = null
    openSectionId.value = null
    openPizzaSectionId.value = null

    editingSectionId.value = null
    addingItemSectionId.value = null
    editingMenuItem.value = null

    managingSubsections.value = false
    rearrangingMenuItems.value = false
    showSubsectionForm.value = false
    editingSubsectionId.value = null

    subsectionForm.value = {
        name: '',
        price: null,
        active: true
    }

    menuStore.clearError()
}

</script>

<style scoped>
.quick-find {
    display: grid;
    gap: 0.5rem;

    margin-top: 1rem;
}

.quick-find>label {
    font-weight: 700;
}

.quick-find-results {
    display: grid;
    gap: 0.4rem;
}

.quick-find-result {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;

    width: 100%;
    padding: 0.7rem 0.8rem;

    text-align: left;
}

.quick-find-result__name {
    font-weight: 700;
}

.quick-find-result__context {
    font-size: 0.8rem;
    opacity: 0.7;
}

.create-card {
    margin: 1rem 0;
}

.section-list {
    display: grid;
    gap: 0.75rem;

    margin-top: 1.5rem;
}

.section-card {
    transition: opacity 180ms ease;
}

.section-card.inactive {
    opacity: 0.65;
}

.section-heading {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;
}

.section-heading h2 {
    margin: 0;
}

.section-subtitle {
    margin: 0.4rem 0 0;
}

.section-footer {
    margin: 1rem 0 0;
}

.section-meta {
    margin-top: 1rem;

    font-size: 0.9rem;
    opacity: 0.7;
}

.create-form {
    grid-template-columns: 1fr;
}

.create-form .admin-form-actions {
    margin-top: 0;
}

.section-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;

    margin-top: 1rem;
}

.section-edit-form {
    grid-template-columns: 1fr;
}

.menu-items-panel {
    margin-top: 1.25rem;
    padding-top: 1.25rem;

    border-top: 1px solid rgba(255, 255, 255, 0.12);
}

.menu-items-panel h3 {
    margin: 0;
}

.menu-item-form-panel {
    margin: 1rem 0;
    padding: 1rem;

    background: rgba(255, 255, 255, 0.04);

    border: 1px solid var(--bronze-color);
    border-radius: 0.5rem;
}

.menu-item-actions {
    display: flex;
    align-items: center;
    gap: 0.75rem;
}

.menu-items-heading-actions {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    flex-wrap: wrap;
}

.menu-item-prices {
    margin: 0.5rem 0 0;
    padding: 0;

    list-style: none;
}

.menu-item-prices li {
    margin-top: 0.2rem;
    font-weight: 700;
}

.menu-item-list {
    display: grid;
    gap: 0.75rem;

    margin-top: 1rem;
}

.menu-item-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;

    padding: 0.75rem;

    border: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 0.4rem;
}

.menu-item-row p {
    margin: 0.35rem 0 0;
}

.item-status {
    font-weight: 700;
}

.item-status.hidden {
    opacity: 0.55;
}

.section-list--grid {
    display: grid;
    gap: 0.75rem;
}

.section-list--workspace {
    display: block;
}

.section-tile {
    min-height: 100px;

    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 0.4rem;

    text-align: center;
}

.section-tile.inactive {
    opacity: 0.55;
}

.section-tile__name {
    font-size: 1.05rem;
}

.section-tile__status {
    font-size: 0.75rem;
    opacity: 0.7;
}

.section-tile--add {
    background: var(--bronze-color);
}

.section-card--workspace {
    width: 100%;
    box-sizing: border-box;
}

.workspace-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;

    margin-bottom: 1rem;
    padding-bottom: 1rem;

    border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.workspace-header h2 {
    margin: 0.2rem 0 0;
}

.workspace-eyebrow {
    font-size: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.08em;
    opacity: 0.65;
}

.workspace-collapse-button {
    flex-shrink: 0;
}

.section-grid-view {
    display: grid;
    grid-template-columns: repeat(auto-fit,
            minmax(150px, 1fr));
    gap: 0.75rem;
}

/* ==========================================================
   ANIMATION EXPAND / COLLAPSE
   ========================================================== */

.section-expand-enter-active,
.section-expand-leave-active {
    transition:
        opacity 120ms ease,
        transform 120ms ease;
}

.section-expand-enter-from,
.section-expand-leave-to {
    opacity: 0;
    transform: scale(0.985) translateY(4px);
}

.section-expand-enter-to,
.section-expand-leave-from {
    opacity: 1;
    transform: scale(1) translateY(0);
}

.subsection-panel {
    margin-top: 1.25rem;
    padding-top: 1.25rem;

    border-top: 1px solid rgba(255, 255, 255, 0.12);
}

.subsection-panel h3 {
    margin: 0;
}

.subsection-help {
    font-size: 0.85rem;
    opacity: 0.7;
}

.subsection-form {
    margin-top: 1rem;
    padding: 1rem;

    background: rgba(255, 255, 255, 0.04);

    border: 1px solid var(--bronze-color);
    border-radius: 0.5rem;
}

.subsection-list {
    display: grid;
    gap: 0.5rem;

    margin-top: 1rem;
}

.subsection-chip {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;

    padding: 0.65rem 0.75rem;

    border: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 0.4rem;
}

.subsection-price {
    margin-left: 0.25rem;
    font-weight: 700;
    opacity: 0.8;
}

.subsection-chip.inactive {
    opacity: 0.55;
}

.subsection-chip-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 0.4rem;
}

.subsection-chip-actions button {
    padding: 0.4rem 0.6rem;
    font-size: 0.8rem;
}

.menu-item-summary {
    display: flex;
    align-items: center;
    gap: 1rem;

    min-width: 0;
}

.menu-item-thumbnail {
    display: flex;
    flex: 0 0 90px;
    align-items: center;
    justify-content: center;

    width: 90px;
    height: 72px;

    overflow: hidden;

    color: var(--text-secondary);
    background: rgba(255, 255, 255, 0.04);

    border: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 0.4rem;

    font-size: 0.75rem;
}

.menu-item-thumbnail img {
    width: 100%;
    height: 100%;

    object-fit: cover;
}

.menu-item-details {
    min-width: 0;
}

.menu-item-photo-action {
    display: block;

    margin: 0.2rem 0 0;
    padding: 0;

    color: var(--bronze-hover);
    background: transparent;

    border: 0;

    font: inherit;
    font-size: 0.75rem;
    font-weight: 700;
    text-align: left;

    cursor: pointer;
}

.menu-item-photo-action:hover {
    color: var(--text-secondary);
}

.menu-item-photo-action:hover:not(:disabled) {
    text-decoration: underline;
}

.menu-item-photo-action:disabled {
    cursor: wait;
    opacity: 0.65;
}

.menu-item-photo-editor {
    display: grid;
    gap: 0.8rem;

    margin-top: 0.75rem;
    padding: 0.85rem;

    background: rgba(255, 255, 255, 0.04);

    border: 1px solid rgba(181, 137, 55, 0.45);
    border-radius: 0.5rem;
}

.recipe-photo-field {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 0.6rem;
}

.recipe-photo-label {
    font-weight: 700;
}

.photo-picker-button {
    width: auto;
}

.photo-filename {
    font-size: 0.85rem;
    opacity: 0.75;
}

.menu-item-photo-editor label {
    display: grid;
    gap: 0.35rem;

    font-weight: 700;
}

.menu-item-photo-editor input {
    width: 100%;
    box-sizing: border-box;
}

.menu-item-photo-editor .admin-form-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;

    margin-top: 0.15rem;
}

.menu-item-photo-editor .admin-form-actions button {
    width: auto;
}

@media (max-width: 600px) {
    .section-heading {
        flex-direction: column;
    }

    .quick-find-result {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.2rem;
    }

    .section-tile {
        min-height: 82px;
        padding: 0.7rem 0.5rem;
    }

    .section-tile__name {
        font-size: 0.9rem;
    }

    .workspace-header {
        flex-direction: column;
        align-items: stretch;
    }

    .workspace-collapse-button {
        align-self: flex-start;
        width: auto;
        padding: 0.45rem 0.65rem;
        font-size: 0.78rem;
    }

    .section-grid-view {
        grid-template-columns: repeat(2,
                minmax(0, 1fr));
        gap: 0.55rem;
    }

    .menu-item-row {
        flex-direction: column;
        align-items: stretch;
        gap: 0.75rem;
    }

    .menu-item-row>div:first-child {
        width: 100%;
        min-width: 0;
    }

    .menu-item-actions {
        width: 100%;
        min-width: 0;

        display: flex;
        flex-wrap: wrap;
        align-items: center;
        gap: 0.4rem;
    }

    .item-status {
        width: 100%;
        margin-bottom: 0.1rem;
    }

    .menu-item-actions button {
        width: auto;
        min-width: 0;

        padding: 0.45rem 0.6rem;

        font-size: 0.78rem;
        line-height: 1.1;
    }

    .menu-item-row strong,
    .menu-item-row p {
        overflow-wrap: anywhere;
    }

    .menu-item-summary {
        align-items: flex-start;
    }

    .menu-item-thumbnail {
        flex-basis: 72px;

        width: 72px;
        height: 60px;
    }

    .menu-item-photo-editor {
        width: 100%;
        box-sizing: border-box;
    }

    .recipe-photo-field {
        align-items: stretch;
    }

    .recipe-photo-label {
        width: 100%;
    }

    .photo-picker-button {
        width: 100%;
    }

    .photo-filename {
        width: 100%;
    }
}
</style>
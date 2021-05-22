<template>
   <div class="item-combination-view">
     <li v-for="(item, index) in questionDetails.items" :key="index">
      <p>
          <span>Item {{index + 1}} with the content '{{item.content}}'</span>
          <v-col>
              <span>Item {{index + 1}} is connected with:</span>
              <v-col v-for="(association, index) in item.connections" :key="index"
                v-if="association.itemTwo !== '' "
              >
                <span>{{association.itemTwo}}</span>
              </v-col>
          </v-col>
      </p>
    </li>
   </div>
 </template>


 <script lang="ts">
 import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
 import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';

 @Component
export default class ItemCombinationView extends Vue {
  @Prop() readonly questionDetails!: ItemCombinationQuestionDetails;
 }
 </script>
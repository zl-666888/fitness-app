Component({
  properties: {
    item: { type: Object, value: {} }
  },
  methods: {
    onTap() { this.triggerEvent('cardtap', { food: this.data.item }) }
  }
})
